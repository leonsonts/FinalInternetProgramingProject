import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Represents a vertex in a social network including relationships with other entities
public class SocialVertex<T> {
    private UUID id;
    private T entity;
    private Map<Class<?>,Map<String,Class<?>>> typeMap;
//    Collection<Profile> myFriends;
//    Map<Long,Profile> myFriends2;
//    Map<UUID,Group> myGroups;
//    Map<String,BusinessPage> myLikedPages;
//    Map<Long,Endorse> groupEndorsement;
    /*
     Create a map of maps. Outer map that maps a class to the class inner map such that
     an inner map gets a key representing an identifier of an object in order to return the actual object
     */

    public SocialVertex(T data){
        this.entity = data;
        this.id = UUID.randomUUID();
        typeMap = new HashMap<>();
    }

    T getEntity(){
        return this.entity;
    }

    <V> Map<String,V> getInnerMap(Class<V> someEntity){
        if (this.typeMap.containsKey(someEntity)){
            return (Map<String, V>) this.typeMap.get(someEntity);
        }
        else{
            return null;
        }
    }

    <V> V putObject(String key,SocialVertex<V> someVertex){
       return ((Map<String,V>)this.typeMap
               .computeIfAbsent(someVertex.getEntity().getClass()
                       ,aClass -> (Map<String, Class<?>>) new HashMap<String,V>()))
               .put(key, someVertex.getEntity());
    }






}
