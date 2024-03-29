// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.adorsys.adpharma.domain.LigneFacture;

privileged aspect LigneFacture_Roo_Json {
    
    public String LigneFacture.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static LigneFacture LigneFacture.fromJsonToLigneFacture(String json) {
        return new JSONDeserializer<LigneFacture>().use(null, LigneFacture.class).deserialize(json);
    }
    
    public static String LigneFacture.toJsonArray(Collection<LigneFacture> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<LigneFacture> LigneFacture.fromJsonArrayToLigneFactures(String json) {
        return new JSONDeserializer<List<LigneFacture>>().use(null, ArrayList.class).use("values", LigneFacture.class).deserialize(json);
    }
    
}
