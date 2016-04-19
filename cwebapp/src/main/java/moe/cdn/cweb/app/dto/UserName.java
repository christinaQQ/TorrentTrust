package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserName {
    private String name;

    public UserName() {
    }

    public UserName(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
