/*
 * Simple Spark web application
 *
 */

<#list classes as class>
import ${name?lower_case}.${class.name};
</#list>
import utils.FreemarkerEngine;
import java.util.HashMap;

import static spark.Spark.*;

public class Application {

    public static void main(String[] args) {


        // Configure Spark
        port(8000);
        staticFiles.externalLocation("src/resources");

        // Configure freemarker engine
        FreemarkerEngine engine = new FreemarkerEngine("src/resources/templates");

        <#list classes as class>
        // Set up endpoints
        get("/", (request, response) -> {
            return engine.render(null, "index.html");
        });

        // Set up Person endpoints
        get("/${class.name?lower_case}/list", (request, response) -> {
            HashMap<Object,Object> n = new HashMap<>();
            n.put("objs",${class.name}.all());
            return engine.render(n,"${name?lower_case}/${class.name?lower_case}/list.html");
        });


        get("/${class.name?lower_case}/get", (request, response) -> {
            HashMap<Object,Object> model = new HashMap<>();
            ${class.name} objct = ${class.name}.get(request.queryParams("id"));
            model.put("obj",objct);
            <#list class.relations as rels>
            <#if rels.foreignClass.name == class.name>
            model.put("relation",objct.get${rels.regularClass.name}s());
            <#else>
            model.put("relation",objct.get${rels.foreignClass.name}());
            </#if>
            </#list>

            return engine.render(model,"${name?lower_case}/${class.name?lower_case}/get.html");
        });


        post("/${class.name?lower_case}/update", (request, response) -> {
            ${class.name?capitalize} obj = ${class.name?capitalize}.get(request.queryParams("id"));

            <#list class.attributes as attr>
            <#if attr.type == "int">
            obj.set${attr.name?capitalize}(Integer.parseInt(request.queryParams("${attr.name?lower_case}")));
            </#if>
            <#if attr.type == "double">
            obj.set${attr.name?capitalize}(Double.parseDouble(request.queryParams("${attr.name?lower_case}")));
            </#if>
            <#if attr.type == "float">
            obj.set${attr.name?capitalize}(Float.parseFloat(request.queryParams("${attr.name?lower_case}")));
            </#if>
            <#if attr.type != "float" && attr.type != "int" && attr.type != "double">
            obj.set${attr.name?capitalize}(request.queryParams("${attr.name?lower_case}"));
            </#if>
            </#list>
            obj.save();

            response.redirect("/${class.name?lower_case}/list");
            return null;
        });

        get("/${class.name?lower_case}/delete", (request, response) -> {
            ${class.name?capitalize} obj = ${class.name?capitalize}.get(request.queryParams("id"));
            obj.delete();

            response.redirect("/${class.name?lower_case}/list");
            return null;
        });

        get("/${class.name?lower_case}/create", (request, response) -> {

            return engine.render(null,"${name?lower_case}/${class.name?lower_case}/create.html");
        });

        post("/${class.name?lower_case}/create", (request, response) -> {
            ${class.name?capitalize} obj = new ${class.name?capitalize}();
            <#list class.attributes as attr>
            <#if attr.type == "int">
            obj.set${attr.name?capitalize}(Integer.parseInt(request.queryParams("${attr.name?lower_case}")));
             </#if>
            <#if attr.type == "double">
            obj.set${attr.name?capitalize}(Double.parseDouble(request.queryParams("${attr.name?lower_case}")));
            </#if>
            <#if attr.type == "float">
            obj.set${attr.name?capitalize}(Float.parseFloat(request.queryParams("${attr.name?lower_case}")));
            </#if>
            <#if attr.type != "float" && attr.type != "int" && attr.type != "double">
            obj.set${attr.name?capitalize}(request.queryParams("${attr.name?lower_case}"));
            </#if>
            </#list>
            obj.save();
            obj.set__name__("${class.name}_"+obj.getId());
            obj.save();
        
            response.redirect("/${class.name?lower_case}/list");
            return null;

        });

        </#list>
    }


}