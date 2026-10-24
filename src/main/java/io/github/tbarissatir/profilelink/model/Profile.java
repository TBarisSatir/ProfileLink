package io.github.tbarissatir.profilelink.model;

import java.util.List;

public class Profile {

    private String id;
    private String name;
    private String headline;
    private List<String> skills;

    public Profile() {
    }

    public Profile(String id, String name, String headline, List<String> skills) {
        this.id = id;
        this.name = name;
        this.headline = headline;
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
