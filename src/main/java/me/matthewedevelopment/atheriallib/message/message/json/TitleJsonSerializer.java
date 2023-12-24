package me.matthewedevelopment.atheriallib.message.message.json;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import me.matthewedevelopment.atheriallib.message.message.MessageTitle;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;


public class TitleJsonSerializer  implements ConfigSerializable<MessageTitle> {
    @Override
    public Map<String, Object> serializeComplex(MessageTitle object) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", object.getMessage());
        if (object.getSubTitle()!=null){
            map.put("subTitle", object.getSubTitle());
        }
        if (object.getStay()>0){
            map.put("stay", object.getStay());
        }
        if (object.getFadeIn()>0){
            map.put("fadeIn", object.getFadeIn());
        }
        if (object.getFadeOut()>0){
            map.put("fadeOut", object.getFadeOut());
        }
        return map;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }

    @Override
    public MessageTitle deserializeComplex(Map<String, Object> map) {
        String message = (String) map.get("title");
        String subTitle = map.containsKey("subTitle") ? (String) map.get("subTitle") : null;

        int stay =  (map.containsKey("stay") ? (int) map.get("stay") : 0);
        int fadeIn =  (map.containsKey("fadeIn") ? (int) map.get("fadeIn") : 0);
        int fadeOut =  (map.containsKey("fadeOut") ? (int) map.get("fadeOut") : 0);
        return new MessageTitle(message,subTitle, stay, fadeIn,fadeOut);
    }



}
