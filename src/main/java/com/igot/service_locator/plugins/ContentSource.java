package com.igot.service_locator.plugins;

public enum ContentSource {
    CORNELL;

    public static ContentSource fromOrgId(String orgId) {
        switch (orgId) {
            case "G00345":
                return CORNELL;
            default:
                throw new RuntimeException("Unknown org id: " + orgId);
        }
    }
}
