import java.lang.String;
import java.lang.System;
import java.util.List;

import com.uploadcare.api.Client;
import com.uploadcare.api.Project;

public class ProjectInfo {

    public static void main(String[] args) {
        Client client = Client.demoClient();
        Project project = client.getProject();

        System.out.println("Name: " + project.getName());
        System.out.println("Public key: " + project.getPubKey());

        List<Project.Collaborator> collaborators = project.getCollaborators();
        if (collaborators.size() > 0) {
            System.out.println("Collaborators:");
            for (Project.Collaborator collaborator : collaborators) {
                System.out.println(" - " + collaborator.getName() + " <" + collaborator.getEmail() + ">");
            }
        }
    }

}
