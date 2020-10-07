package br.ufba.jnose.pages.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.Link;

public abstract class LinkResult extends Link<String> {

    public LinkResult(String id) {
        super(id);
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();

    }


}
