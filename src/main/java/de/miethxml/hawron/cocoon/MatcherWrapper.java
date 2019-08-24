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
package de.miethxml.hawron.cocoon;

import java.util.ArrayList;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class MatcherWrapper {
    private String match;
    private String serializer;
    private GeneratorWrapper generator;
    private ArrayList transformer;
    private SitemapWrapper subSitemap;
    private SitemapWrapper sitemap;

    /**
     *
     *
     *
     */
    public MatcherWrapper() {
        super();

        serializer = "";

        transformer = new ArrayList();
    }

    /**
     *
     * @return
     */
    public String getMatch() {
        return match;
    }

    /**
     *
     * @param match
     *
     */
    public void setMatch(String match) {
        this.match = match;
    }

    /**
     *
     * @return
     */
    public String getSerializer() {
        return serializer;
    }

    /**
     *
     * @param serializer
     *
     */
    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    /**
     *
     * @return
     */
    public GeneratorWrapper getGenerator() {
        return generator;
    }

    /**
     *
     * @param generator
     *
     */
    public void setGenerator(GeneratorWrapper generator) {
        this.generator = generator;
    }

    public void addTransformer(TransformerWrapper tw) {
        transformer.add(tw);
    }

    public int getTransformerCount() {
        return this.transformer.size();
    }

    public TransformerWrapper gettransformer(int index) {
        if ((index > -1) && (index < transformer.size())) {
            return (TransformerWrapper) transformer.get(index);
        }

        return null;
    }

    public void setSitemap(SitemapWrapper sitemap) {
        this.sitemap = sitemap;
    }

    public boolean isMountSubsitemap() {
        if (subSitemap != null) {
            return true;
        }

        return false;
    }
}
