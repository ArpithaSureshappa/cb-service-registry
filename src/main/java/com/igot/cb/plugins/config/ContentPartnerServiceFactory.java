package com.igot.cb.plugins.config;

import com.igot.cb.plugins.ContentPartnerPluginService;
import com.igot.cb.plugins.ContentSource;

public interface ContentPartnerServiceFactory {
    ContentPartnerPluginService getContentPartnerPluginService(ContentSource contentSource);
}
