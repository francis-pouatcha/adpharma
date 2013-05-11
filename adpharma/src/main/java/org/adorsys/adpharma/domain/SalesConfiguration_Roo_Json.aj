// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.adorsys.adpharma.domain.SalesConfiguration;

privileged aspect SalesConfiguration_Roo_Json {
    
    public String SalesConfiguration.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static SalesConfiguration SalesConfiguration.fromJsonToSalesConfiguration(String json) {
        return new JSONDeserializer<SalesConfiguration>().use(null, SalesConfiguration.class).deserialize(json);
    }
    
    public static String SalesConfiguration.toJsonArray(Collection<SalesConfiguration> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<SalesConfiguration> SalesConfiguration.fromJsonArrayToSalesConfigurations(String json) {
        return new JSONDeserializer<List<SalesConfiguration>>().use(null, ArrayList.class).use("values", SalesConfiguration.class).deserialize(json);
    }
    
}
