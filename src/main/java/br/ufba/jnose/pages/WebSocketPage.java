package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.TextMessage;

/**
 * Tentando seguir: https://cwiki.apache.org/confluence/display/WICKET/Wicket+Native+WebSockets
 */
public class WebSocketPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public WebSocketPage() {

    }

}