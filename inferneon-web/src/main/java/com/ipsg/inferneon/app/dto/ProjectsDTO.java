package com.ipsg.inferneon.app.dto;

import java.util.List;

/**
 *
 * JSON serializable DTO containing data concerning a project search request.
 *
 */
public class ProjectsDTO {

    private long currentPage;
    private long totalPages;
    List<ProjectDTO> projects;

    public ProjectsDTO(long currentPage, long totalPages, List<ProjectDTO> projects) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.projects = projects;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }
}
