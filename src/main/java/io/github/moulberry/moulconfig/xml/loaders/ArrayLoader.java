package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.component.ArrayComponent;
import io.github.moulberry.moulconfig.observer.GetSetter;
import io.github.moulberry.moulconfig.observer.ObservableList;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class ArrayLoader implements XMLGuiLoader<GuiComponent> {
    @Override
    public GuiComponent createInstance(XMLContext<?> context, Element element) {
        GetSetter<ObservableList> list = context.getPropertyFromAttribute(element, new QName("data"), ObservableList.class);
        return new ArrayComponent<Object>(
                list.get(),
                object -> context.getChildFragment(element, object)
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Array");
    }
}
