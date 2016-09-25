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
package org.kabeja.processing;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.helper.MergeMap;
import org.kabeja.tools.SAXFilterConfig;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessPipeline {
	private ProcessingManager manager;
	private List<PostProcessorConfig> postProcessorConfigs = new ArrayList<PostProcessorConfig>();
	private List<SAXFilterConfig> saxFilterConfigs = new ArrayList<SAXFilterConfig>();
	private SAXGenerator generator;
	private Map<String, Object> serializerProperties = new HashMap<String, Object>();
	private Map<String, Object> generatorProperties = new HashMap<String, Object>();
	private SAXSerializer serializer;
	private String name;
	private String description = StringUtils.EMPTY;

	public void process(DXFDocument doc, Map<String, Object> context, OutputStream out)
			throws ProcessorException {
		ContentHandler handler = null;

		// postprocess
		Iterator<PostProcessorConfig> i = this.postProcessorConfigs.iterator();

		while (i.hasNext()) {
			PostProcessorConfig ppc = i.next();
			PostProcessor pp = this.manager.getPostProcessor(ppc
					.getPostProcessorName());

			// backup the default props
			Map<String, Object> oldProps = pp.getProperties();
			// setup the pipepine props
			pp.setProperties(new MergeMap<String, Object>(ppc.getProperties(), context));
			pp.process(doc, context);
			// restore the default props
			pp.setProperties(oldProps);
		}

		List<Map<String, Object>> saxFilterProperties = new ArrayList<Map<String, Object>>();

		// setup saxfilters
		if (this.saxFilterConfigs.size() > 0) {
			Iterator<SAXFilterConfig> i2 = saxFilterConfigs.iterator();
			SAXFilterConfig sc = i2.next();
			SAXFilter first = this.manager.getSAXFilter(sc.getFilterName());
			saxFilterProperties
					.add(new MergeMap<String, Object>(first.getProperties(), context));

			first.setContentHandler(this.serializer);
			handler = first;
			first.setProperties(sc.getProperties());

			while (i2.hasNext()) {
				sc = i2.next();
				SAXFilter f = this.manager.getSAXFilter(sc.getFilterName());
				// BUG! f.setContentHandler(first);
				f.setContentHandler(handler);
				saxFilterProperties.add(f.getProperties());
				f.setProperties(sc.getProperties());
				// BUG! first = f;
				handler = f;

			}

		} else {
			// no filter
			handler = this.serializer;
		}

		Map<String, Object> oldProbs = this.serializer.getProperties();
		this.serializer.setProperties(new MergeMap<String, Object>(this.serializerProperties,
				context));

		// invoke the filter and serializer
		this.serializer.setOutput(out);

		try {
			Map<String, Object> oldGenProps = this.generator.getProperties();
			this.generator.setProperties(this.generatorProperties);
			this.generator.generate(doc, handler, context);
			// restore the old props
			this.generator.setProperties(oldGenProps);
		} catch (SAXException e) {
			throw new ProcessorException(e);
		}

		// restore the serializer properties
		this.serializer.setProperties(oldProbs);

		// restore the filter properties
		for (int x = 0; x < saxFilterProperties.size(); x++) {
			SAXFilterConfig sc = saxFilterConfigs.get(x);
			this.manager.getSAXFilter(sc.getFilterName()).setProperties(
					saxFilterProperties.get(x));
		}
	}

	/**
	 * @return Returns the serializer.
	 */
	public SAXSerializer getSAXSerializer() {
		return serializer;
	}

	/**
	 * @param serializer
	 *            The serializer to set.
	 */
	public void setSAXSerializer(SAXSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * @return Returns the manager.
	 */
	public ProcessingManager getProcessorManager() {
		return manager;
	}

	/**
	 * @param manager
	 *            The manager to set.
	 */
	public void setProcessorManager(ProcessingManager manager) {
		this.manager = manager;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void prepare() {
	}

	public List<PostProcessorConfig> getPostProcessorConfigs() {
		return this.postProcessorConfigs;
	}

	public void addSAXFilterConfig(SAXFilterConfig config) {
		this.saxFilterConfigs.add(config);
	}

	public void addPostProcessorConfig(PostProcessorConfig config) {
		this.postProcessorConfigs.add(config);
	}

	/**
	 * @return Returns the serializerProperties.
	 */
	public Map<String, Object> getSerializerProperties() {
		return serializerProperties;
	}

	/**
	 * @param serializerProperties
	 *            The serializerProperties to set.
	 */
	public void setSAXSerializerProperties(Map<String, Object> serializerProperties) {
		this.serializerProperties = serializerProperties;
	}

	public void setSAXGeneratorProperties(Map<String, Object> generatorProperties) {
		this.generatorProperties = generatorProperties;
	}

	public Map<String, Object> getSAXGeneratorProperties(Map<String, Object> generatorProperties) {
		return this.generatorProperties;
	}

	public void setSAXGenerator(SAXGenerator generator) {
		this.generator = generator;
	}

	public SAXGenerator getSAXGenerator() {
		return this.generator;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
