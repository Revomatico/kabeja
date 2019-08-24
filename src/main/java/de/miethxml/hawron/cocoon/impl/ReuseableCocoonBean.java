/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.miethxml.hawron.cocoon.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.miethxml.hawron.project.ProcessURI;
import de.miethxml.hawron.project.Task;

import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.bean.BeanListener;
import org.apache.cocoon.bean.CocoonWrapper;
import org.apache.cocoon.bean.Target;
import org.apache.cocoon.bean.helpers.Crawler;
import org.apache.cocoon.bean.helpers.DelayedOutputStream;
import org.apache.cocoon.components.notification.DefaultNotifyingBuilder;
import org.apache.cocoon.components.notification.Notifier;
import org.apache.cocoon.components.notification.Notifying;
import org.apache.cocoon.components.notification.SimpleNotifyingBean;
import org.apache.cocoon.matching.helpers.WildcardHelper;
import org.apache.commons.logging.LogFactory;

import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;

/**
 * @author simon
 * 
 */
public class ReuseableCocoonBean extends CocoonWrapper {
	// User Supplied Parameters
	private boolean followLinks = true;

	private boolean precompileOnly = false;

	private boolean confirmExtension = true;

	private String defaultFilename = Constants.INDEX_URI;

	private boolean brokenLinkGenerate = false;

	private String brokenLinkExtension = "";

	private List excludePatterns = new ArrayList();

	private List includePatterns = new ArrayList();

	private List includeLinkExtensions = null;

	// Internal Objects
	private boolean init;

	private List listeners = new ArrayList();

	private boolean verbose;

	private SourceResolver sourceResolver;

	private Crawler crawler;

	private String checksumsURI = null;

	private Map checksums;

	private boolean interrupted = false;

	//
	// INITIALISATION METHOD
	//
	public void initialize() throws Exception {
		if (this.init == false) {
			super.initialize();

			this.sourceResolver = (SourceResolver) getComponentManager()
					.lookup(SourceResolver.ROLE);

			init = true;
		}
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	//
	// GETTERS AND SETTERS FOR CONFIGURATION PROPERTIES
	//
	public void setFollowLinks(boolean follow) {
		followLinks = follow;
	}

	public void setConfirmExtensions(boolean confirmExtension) {
		this.confirmExtension = confirmExtension;
	}

	public void setPrecompileOnly(boolean precompileOnly) {
		this.precompileOnly = precompileOnly;
	}

	public boolean isPrecompileOnly() {
		return precompileOnly;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setDefaultFilename(String filename) {
		defaultFilename = filename;
	}

	public void setBrokenLinkGenerate(boolean brokenLinkGenerate) {
		this.brokenLinkGenerate = brokenLinkGenerate;
	}

	public void setBrokenLinkExtension(String brokenLinkExtension) {
		this.brokenLinkExtension = brokenLinkExtension;
	}

	public void setChecksumURI(String uri) {
		this.checksumsURI = uri;
	}

	public boolean followLinks() {
		return followLinks;
	}

	public boolean confirmExtensions() {
		return confirmExtension;
	}

	public int getTargetCount() {
		return crawler.getRemainingCount();
	}

	public void addExcludePattern(String pattern) {
		int[] preparedPattern = WildcardHelper.compilePattern(pattern);
		excludePatterns.add(preparedPattern);
	}

	public void addIncludePattern(String pattern) {
		int[] preparedPattern = WildcardHelper.compilePattern(pattern);
		includePatterns.add(preparedPattern);
	}

	public void addIncludeLinkExtension(String extension) {
		if (includeLinkExtensions == null) {
			includeLinkExtensions = new ArrayList();
		}

		includeLinkExtensions.add(extension);
	}

	public void addListener(BeanListener listener) {
		this.listeners.add(listener);
	}

	public void pageGenerated(String sourceURI, String destURI, int pageSize,
			int linksInPage, int newLinksInPage, int pagesRemaining,
			int pagesComplete, long timeTaken) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.pageGenerated(sourceURI, destURI, pageSize, linksInPage,
					newLinksInPage, pagesRemaining, pagesComplete, timeTaken);
		}
	}

	public void sendMessage(String msg) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.messageGenerated(msg);
		}
	}

	public void sendWarning(String uri, String warning) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.warningGenerated(uri, warning);
		}
	}

	public void sendBrokenLinkWarning(String uri, String warning) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.brokenLinkFound(uri, "", warning, null);
		}
	}

	public void pageSkipped(String uri, String message) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.pageSkipped(uri, message);
		}
	}

	public void dispose() {

		if (this.init) {
			if (this.sourceResolver != null) {
				getComponentManager().release(this.sourceResolver);
				this.sourceResolver = null;
			}

			super.dispose();
		}
	}

	/**
	 * Processes the given Target and return all links.
	 * 
	 * If links are to be followed, and extensions checked then the algorithm is
	 * as follows:
	 * <ul>
	 * <li>file name for the URI is generated. URI MIME type is checked for
	 * consistency with the URI and, if the extension is inconsistent or absent,
	 * the file name is changed</li>
	 * <li>the link view of the given URI is called and the file names for
	 * linked resources are generated and stored.</li>
	 * <li>for each link, absolute file name is translated to relative path.
	 * </li>
	 * <li>after the complete list of links is translated, the link-translating
	 * view of the resource is called to obtain a link-translated version of the
	 * resource with the given link map</li>
	 * <li>list of absolute URI is returned, for every URI which is not yet
	 * present in list of all translated URIs</li>
	 * </ul>
	 * 
	 * If links are to be followed, but extensions are not checked, then the
	 * algorithm will be:
	 * <ul>
	 * <li>The content for the page is generated</li>
	 * <li>Whilst generating, all links are gathered by the LinkGatherer</li>
	 * <li>Gathered links are added to the unprocessed links list, and
	 * processing continues until all processing is complete</li>
	 * </ul>
	 * 
	 * @param target
	 *            a <code>Target</code> target to process
	 * @exception Exception
	 *                if an error occurs
	 */
	private void processTarget(Crawler crawler, Target target) throws Exception {
		int status = 0;

		int linkCount = 0;
		int newLinkCount = 0;
		int pageSize = 0;
		long startTimeMillis = System.currentTimeMillis();

		if (target.confirmExtensions()) {
			if (!crawler.hasTranslatedLink(target)) {
				final String mimeType = getType(target
						.getDeparameterizedSourceURI(), target.getParameters());
				target.setMimeType(mimeType);
				crawler.addTranslatedLink(target);
			}
		}

		// Process links
		final HashMap translatedLinks = new HashMap();

		if (target.followLinks() && target.confirmExtensions()
				&& isCrawlablePage(target)) {
			final Iterator i = this.getLinks(
					target.getDeparameterizedSourceURI(),
					target.getParameters()).iterator();

			while (i.hasNext()) {
				String linkURI = (String) i.next();
				Target linkTarget = target.getDerivedTarget(linkURI);

				if (linkTarget == null) {
					pageSkipped(linkURI,
							"link does not share same root as parent");

					continue;
				}

				if (!isIncluded(linkTarget.getSourceURI())) {
					pageSkipped(linkTarget.getSourceURI(),
							"matched include/exclude rules");

					continue;
				}

				if (!crawler.hasTranslatedLink(linkTarget)) {
					try {
						final String mimeType = getType(linkTarget
								.getDeparameterizedSourceURI(), linkTarget
								.getParameters());
						linkTarget.setMimeType(mimeType);
						crawler.addTranslatedLink(linkTarget);
						log.info("  Link translated: "
								+ linkTarget.getSourceURI());

						if (crawler.addTarget(linkTarget)) {
							newLinkCount++;
						}
					} catch (ProcessingException pe) {
						this.sendBrokenLinkWarning(linkTarget.getSourceURI(),
								pe.getMessage());

						if (this.brokenLinkGenerate) {
							if (crawler.addTarget(linkTarget)) {
								newLinkCount++;
							}
						}
					}
				} else {
					String originalURI = linkTarget.getOriginalSourceURI();
					linkTarget = crawler.getTranslatedLink(linkTarget);
					linkTarget.setOriginalURI(originalURI);
				}

				translatedLinks.put(linkTarget.getOriginalSourceURI(),
						linkTarget.getTranslatedURI(target.getPath()));
			}

			linkCount = translatedLinks.size();
		}

		try {
			// Process URI
			DelayedOutputStream output = new DelayedOutputStream();

			try {
				List gatheredLinks;

				if (!target.confirmExtensions() && target.followLinks()
						&& isCrawlablePage(target)) {
					gatheredLinks = new ArrayList();
				} else {
					gatheredLinks = null;
				}

				status = getPage(target.getDeparameterizedSourceURI(),
						getLastModified(target), target.getParameters(), target
								.confirmExtensions() ? translatedLinks : null,
						gatheredLinks, output);

				if (status >= 400) {
					throw new ProcessingException("Resource not found: "
							+ status);
				}

				if (gatheredLinks != null) {
					for (Iterator it = gatheredLinks.iterator(); it.hasNext();) {
						String linkURI = (String) it.next();
						Target linkTarget = target.getDerivedTarget(linkURI);

						if (linkTarget == null) {
							pageSkipped(linkURI,
									"link does not share same root as parent");

							continue;
						}

						if (!isIncluded(linkTarget.getSourceURI())) {
							pageSkipped(linkTarget.getSourceURI(),
									"matched include/exclude rules");

							continue;
						}

						if (crawler.addTarget(linkTarget)) {
							newLinkCount++;
						}
					}

					linkCount = gatheredLinks.size();
				}
			} catch (ProcessingException pe) {
				output.close();
				output = null;
				this.resourceUnavailable(target);
				this.sendBrokenLinkWarning(target.getSourceURI(),
						DefaultNotifyingBuilder.getRootCause(pe).getMessage());
			} finally {
				if ((output != null) && (status != -1)) {
					ModifiableSource source = getSource(target);

					try {
						pageSize = output.size();

						if ((this.checksumsURI == null)
								|| !isSameContent(output, target)) {
							OutputStream stream = source.getOutputStream();
							output.setFileOutputStream(stream);
							output.flush();
							output.close();
							pageGenerated(target.getSourceURI(), target
									.getAuthlessDestURI(), pageSize, linkCount,
									newLinkCount, crawler.getRemainingCount(),
									crawler.getProcessedCount(), System
											.currentTimeMillis()
											- startTimeMillis);
						} else {
							output.close();
							pageSkipped(target.getSourceURI(),
									"Page not changed");
						}
					} catch (IOException ioex) {
						log.warn(ioex.toString());
					} finally {
						releaseSource(source);
					}
				}
			}
		} catch (Exception rnfe) {
			log.warn("Could not process URI: " + target.getSourceURI());
			rnfe.printStackTrace();
			this.sendBrokenLinkWarning(target.getSourceURI(), "URI not found: "
					+ rnfe.getMessage());
		}
	}

	/**
	 * Generate a <code>resourceUnavailable</code> message.
	 * 
	 * @param target
	 *            being unavailable
	 * @exception IOException
	 *                if an error occurs
	 */
	private void resourceUnavailable(Target target) throws IOException,
			ProcessingException {
		if (brokenLinkGenerate) {
			// Why decode this URI now?
			// String brokenFile = NetUtils.decodePath(destinationURI);
			if (brokenLinkExtension != null) {
				target.setExtraExtension(brokenLinkExtension);
			}

			SimpleNotifyingBean n = new SimpleNotifyingBean(this);
			n.setType("resource-not-found");
			n.setTitle("Resource not Found");
			n.setSource("Cocoon commandline (Main.java)");
			n.setMessage("Page Not Available.");
			n.setDescription("The requested resource couldn't be found.");
			n.addExtraDescription(Notifying.EXTRA_REQUESTURI, target
					.getSourceURI());
			n.addExtraDescription("missing-file", target.getSourceURI());

			ModifiableSource source = getSource(target);

			try {
				OutputStream stream = source.getOutputStream();

				PrintStream out = new PrintStream(stream);
				Notifier.notify(n, out, "text/html");
				out.flush();
				out.close();
			} finally {
				releaseSource(source);
			}
		}
	}

	public ModifiableSource getSource(Target target) throws IOException,
			ProcessingException {
		final String finalDestinationURI = target.getDestinationURI();
		Source src = sourceResolver.resolveURI(finalDestinationURI);

		if (!(src instanceof ModifiableSource)) {
			sourceResolver.release(src);
			throw new ProcessingException("Source is not Modifiable: "
					+ finalDestinationURI);
		}

		return (ModifiableSource) src;
	}

	public long getLastModified(Target target) throws IOException,
			ProcessingException {
		Source src = getSource(target);
		long lastModified = src.getLastModified();
		this.releaseSource(src);

		return lastModified;
	}

	public void releaseSource(Source source) {
		sourceResolver.release(source);
	}

	private boolean isIncluded(String uri) {
		boolean included;
		Iterator i;
		HashMap map = new HashMap();

		if (includePatterns.size() == 0) {
			included = true;
		} else {
			included = false;
			i = includePatterns.iterator();

			while (i.hasNext()) {
				int[] pattern = (int[]) i.next();

				if (WildcardHelper.match(map, uri, pattern)) {
					included = true;

					break;
				}
			}
		}

		if (excludePatterns.size() != 0) {
			i = excludePatterns.iterator();

			while (i.hasNext()) {
				int[] pattern = (int[]) i.next();

				if (WildcardHelper.match(map, uri, pattern)) {
					included = false;

					break;
				}
			}
		}

		return included;
	}

	private boolean isCrawlablePage(Target target) {
		if (includeLinkExtensions == null) {
			return true;
		} else {
			return includeLinkExtensions.contains(target.getExtension());
		}
	}

	/*
	 * NB. This is a temporary solution - it may well be replaced by storing the
	 * checksum info in the XML 'report' file, along with details of what pages
	 * were created, etc.
	 */
	private void readChecksumFile() throws Exception {
		checksums = new HashMap();

		try {
			Source checksumSource = sourceResolver.resolveURI(checksumsURI);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					checksumSource.getInputStream()));
			String line;
			int lineNo = 0;

			while ((line = reader.readLine()) != null) {
				lineNo++;

				if (line.trim().startsWith("#") || (line.trim().length() == 0)) {
					continue;
				}

				if (line.indexOf("\t") == -1) {
					throw new ProcessingException("Missing tab at line "
							+ lineNo + " of " + checksumsURI);
				}

				String filename = line.substring(0, line.indexOf("\t"));
				String checksum = line.substring(line.indexOf("\t") + 1);
				checksums.put(filename, checksum);
			}

			reader.close();
		} catch (SourceNotFoundException e) {
			// return leaving checksums map empty
		}
	}

	private void writeChecksumFile() throws Exception {
		
		
		
		synchronized (this) {
			Source checksumSource = sourceResolver.resolveURI(checksumsURI);

			if (!(checksumSource instanceof ModifiableSource)) {
				throw new ProcessingException(
						"Checksum file is not Modifiable:" + checksumSource);
			}

			ModifiableSource source = (ModifiableSource) checksumSource;
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(source
					.getOutputStream()));
			Iterator i = checksums.keySet().iterator();

			while (i.hasNext()) {
				String key = (String) i.next();
				String checksum = (String) checksums.get(key);
				writer.println(key + "\t" + checksum);
			}

			writer.close();

			sourceResolver.release(checksumSource);
		}
	}

	private boolean isSameContent(DelayedOutputStream stream, Target target) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(stream.getContent());

			String streamDigest = SourceUtil.encodeBASE64(new String(md5
					.digest()));
			String targetDigest = (String) checksums.get(target.getSourceURI());

			if (streamDigest.equals(targetDigest)) {
				return true;
			} else {
				checksums.put(target.getSourceURI(), streamDigest);

				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			// or do something:
			return false;
		}
	}

	/**
	 * Print a description of the software before running
	 */
	public static String getProlog() {
		String lSep = System.getProperty("line.separator");
		StringBuffer msg = new StringBuffer();
		msg
				.append(
						"------------------------------------------------------------------------ ")
				.append(lSep);
		msg.append(Constants.NAME).append(" ").append(Constants.VERSION)
				.append(lSep);
		msg.append("Copyright (c) ").append(Constants.YEAR).append(
				" Apache Software Foundation. All rights reserved.").append(
				lSep);
		msg
				.append(
						"------------------------------------------------------------------------ ")
				.append(lSep).append(lSep);

		return msg.toString();
	}

	public void process(Task task, String logger) throws Exception {
		this.crawler = new Crawler();

		Iterator i = task.getProcessURI().iterator();

		while (i.hasNext()) {
			ProcessURI uri = (ProcessURI) i.next();

			Target target = null;

			if (uri.getSrcPrefix().length() > 0) {
				target = new Target(uri.getType(), uri.getSrcPrefix(), uri
						.getUri(), task.getBuildDir());
			} else {
				target = new Target(uri.getType(), uri.getUri(), uri.getDest());
			}

			target.setFollowLinks(task.isFollowLinks());
			target.setConfirmExtension(task.isConfirmExtensions());
			target.setLogger(logger);
			crawler.addTarget(target);
		}

		this.interrupted = false;

		if (!this.init) {
			this.initialize();
		}

		if (this.checksumsURI != null) {
			readChecksumFile();
		}

		if (crawler.getRemainingCount() > 0) {
			Iterator iterator = crawler.iterator();

			while (iterator.hasNext() && !this.interrupted) {
				Target target = (Target) iterator.next();

				if (!precompileOnly) {
					processTarget(crawler, target);
				}
			}
		}

		if (this.checksumsURI != null) {
			writeChecksumFile();
		}

		if (this.interrupted) {
			sendMessage("Processing interrupted, pages left: "
					+ crawler.getRemainingCount());
		}

		// maybe this is missing in the default process()
		sendComplete();
	}

	/**
	 * <p>
	 * You can interrupt the processing (only the process(Crawler crawler)) from
	 * a controlling Thread, but note CocoonBean is not threadsafe. Only the
	 * processing is interrupted not the initialization. After successfully
	 * interruption the CocoonBean will send the "complete"-Event to the
	 * BeanListeners, so you have to capture this.
	 * </p>
	 */
	public void interruptProcessing() {
		this.interrupted = true;
	}

	public void sendComplete() {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			BeanListener l = (BeanListener) i.next();
			l.complete();
		}
	}
}
