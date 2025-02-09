[![Download](https://api.bintray.com/packages/raisercostin/maven/kabeja/images/download.svg)](https://bintray.com/raisercostin/maven/kabeja/_latestVersion)

Kabeja is a small library for parsing DXF-Files and converting
this to SVG. It is licensed under the Apache Software License 2.0.


Limitation:
-----------
There are not all Entities of DXF supported yet. Text-Entities generate problems too.

Supported:

 
 *Arc
 *Attrib
 *Polyline
 *Circle
 *Line
 *Blocks/Insert
 *Text
 *MText
 *LWPolyline
 *Solid
 *Trace
 *Ellipse
 *Dimension
 *Image
 *Leader
 *XLine
 *Ray
 *Hatch
 *Spline
 *MLine

Planned:


 
 * Tolerance


You can use Kabeja from CLI (Command Line Interface) or embed in your application. 

GUI:
----
Windows: 
		* double-click "kabeja.exe"

Linux: 
   		* sh kabeja.sh
   		
   		or:
   		
   		* chmod a=rx kabeja.sh (only ones)
   		* ./kabeja.sh
   		
Other:
       * java -jar launcher.jar



CLI:
----
in the Kabeja-folder try:

  * Help and pipeline list
  
     java -jar launcher.jar --help 
  
  * Convert to svg

    java -jar launcher.jar -nogui -pipeline svg myfile.dxf result.svg
  
  * Convert to pdf|jpeg|png|...
  
      java -jar launcher.jar -nogui -pipeline <pdf|jpeg|png>  myfile.dxf 
  
  
Normally Java uses 64 MB of your memory, to setup more use the following commandline
switch:  

java -Xmx256m  -jar ..... 
  

  
GUI-Viewer:
-----------
in the 'lib'-folder try:

  java -jar kabeja-svgview.jar
  
  
  
Cocoon 2.1 (XML-Publishing-Framework http://cocoon.apache.org/2.1):
-------------------------------------------------------------------

Copy the 'kabeja.jar' and 'kabeja-svg2dxf-cocoon.jar' to your WEB-INF/lib-folder
of your Cocoon-Webapplication. Then you can use Kabeja as Generator like:


in your sitemap/subsitemap:

snippet:
--------

<map:components>
     .....   
 <map:generators default="file">
    
  <map:generator name="dxf2svg" src="org.kabeja.cocoon.generation.DXF2SVGGenerator"/>
    
 </map:generators>

  
....
 <map:pipelines>
   
   <map:pipeline>
    
     <map:match pattern="dxf/*.svg">
        <map:generate type="dxf2svg" src="dxf/{1}.dxf"/>
	  ...
	   <!-- transform things you need -->
	
	<map:serialize type="xml"/>
     </map:match>
    
    </map:pipeline>
   
   ....
   
   
   </map:pipelines>

Note: DXF-drafts often real large drafts, so the SVGDocument will consume a lot of memory. The Generator is 
Cacheable so the first run will take more time.




Feedback and Help
-----------------

Any help and feedback are greatly appreciated.

Mail: simon.mieth@gmx.de


# Configuration

## Usage

### Maven

#### Dependency
See released versions at https://bintray.com/beta/#/raisercostin/maven/connector-scripted-sql

```
<dependency>
  <groupId>org.kabeja</groupId>
  <artifactId>kabeja</artifactId>
  <version>0.4.7</version>
</dependency>
```

#### Repository
```
<repository>
  <id>raisercostin-bintray</id>
  <url>https://dl.bintray.com/raisercostin/maven</url>
  <releases><enabled>true</enabled></releases>
  <snapshots><enabled>false</enabled></snapshots>
</repository>
```

## Development

- To release `mvn release:prepare release:perform -DskipTests=true -Prelease -Darguments="-DskipTests=true -Prelease"`

# Resources
- http://kabeja.sourceforge.net/examples.html
- https://sourceforge.net/projects/kabeja/
- https://sourceforge.net/p/kabeja/discussion/510351/
- https://sourceforge.net/p/kabeja/code/HEAD/tree/
- https://sourceforge.net/projects/kabeja/files/kabeja-inkscape-extension/kabeja-inkscape-extension-0.4/
- https://sourceforge.net/p/kabeja/bugs/
- https://sourceforge.net/p/kabeja/wiki/Home/
- http://kabeja.sourceforge.net/docs/user/userguide.html