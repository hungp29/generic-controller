package org.example.genericcontroller.support.generic.dto;

public class ResolvedDTO {

    private ResolvedDTO() {
    }

    public static ResolvedDTO newInstance() {
        return ResolvedDTOHelper.INSTANCE;
    }

    private static class ResolvedDTOHelper {
        private static ResolvedDTO INSTANCE = new ResolvedDTO();
    }
}
