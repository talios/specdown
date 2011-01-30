package com.theoryinpractise.specdown.concordion;

import org.concordion.api.Element;

import java.util.HashMap;
import java.util.Map;

public class SpecException extends RuntimeException {

    Map<Integer, FactModifier> groupChanges = new HashMap<Integer, FactModifier>();


    public Map<Integer, FactModifier> getGroupChanges() {
        return groupChanges;
    }

    public FactGroupModifier modifyGroup(int i) {
        return new FactGroupModifier(this, i);
    }

    public interface FactModifier {
        void modify(Element element, String original);
    }

    public class SimpleModifier implements FactModifier {

        private String elementType;

        public SimpleModifier(String element) {
            this.elementType = element;
        }

        public void modify(Element element, String original) {
            element.appendChild(newElement(elementType, "", original));
        }
    }

    public class ExpectedModifier implements FactModifier {
        private String text;

        public ExpectedModifier(String text) {
            this.text = text;
        }

        public void modify(Element element, String original) {
            element.appendChild(newElement("del", "expected", original));
            element.appendChild(newElement("ins", "actual", text));
        }
    }

    public class FactGroupModifier {

        private SpecException exception;
        private Integer group;

        public FactGroupModifier(SpecException e, int group) {
            this.exception = e;
            this.group = group;
        }

        public SpecException withEmphasis() {
            exception.groupChanges.put(group, new SimpleModifier("i"));
            return exception;
        }

        public SpecException withExpectedText(String text) {
            exception.groupChanges.put(group, new ExpectedModifier(text));
            return exception;
        }

    }

    private Element newElement(final String type, String style, String content) {
        Element evalTitle = new Element(type);
        evalTitle.appendText(content);
        evalTitle.addStyleClass(style);
        return evalTitle;
    }

}
