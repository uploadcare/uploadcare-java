package com.uploadcare.data;

import java.util.List;

public class ProjectData {

    public String name;
    public String pubKey;

    public List<CollaboratorData> collaborators;
    public class CollaboratorData {
        public String name;
        public String email;
    }

    @Override
    public String toString() {
        return "ProjectData{" +
                "name='" + name + '\'' +
                ", pubKey='" + pubKey + '\'' +
                ", collaborators=" + collaborators +
                '}';
    }
}
