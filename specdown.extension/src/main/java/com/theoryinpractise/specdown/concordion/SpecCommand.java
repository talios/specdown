package com.theoryinpractise.specdown.concordion;

import org.concordion.api.*;
import org.concordion.internal.util.Check;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecCommand extends AbstractCommand {

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside a 'spec' is not supported");

        Element element = commandCall.getElement();
        String text = element.getText();

        Matcher argMatcher = Pattern.compile("#.*\\w").matcher(text);
        Map<String, String> args = new HashMap<String, String>();
        while (argMatcher.find()) {
            String arg = text.substring(argMatcher.start(), argMatcher.end());
            args.put(arg, String.valueOf(evaluator.getVariable(arg)));
        }

        for (Map.Entry<String, String> entry : args.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }

        try {
            Object result = findEvalSourceObject(evaluator, element);

            Method[] methods = result.getClass().getDeclaredMethods();

            for (Method method : methods) {
                Spec spec = method.getAnnotation(Spec.class);
                if (spec != null) {

                    Matcher matcher = Pattern.compile(spec.value()).matcher(text);

                    if (matcher.matches()) {
                        if (method.getParameterTypes().length == matcher.groupCount()) {

                            try {

                                Object[] params = new Object[method.getParameterTypes().length];
                                for (int i = 0; i < method.getParameterTypes().length; i++) {
                                    params[i] = method.getParameterTypes()[i].getConstructor(String.class).newInstance(matcher.group(i + 1));
                                }

                                Object specResult = method.invoke(result, params);

                                handleSuccess(element, text, spec.value());

                                if (specResult != null) {
                                    element.appendChild(extractResult(specResult));
                                }

                                return;

                            } catch (InvocationTargetException e) {

                                if (e.getCause() instanceof SpecException) {
                                   handleFailure(element, ((SpecException) e.getCause()).getGroupChanges(), text, spec.value());
                                   return;
                                } else {
                                    handleFailure(element, text, e.getCause().toString());
                                    throw e.getCause();
                                }
                            } catch (Exception e) {
                                handleFailure(element, e.getMessage(), e.getCause().toString());
                                throw e;
                            }

                        } else {
                            final String message = String.format("Method %s has %s parameters, but spec only found %s matching groups.",
                                    method.getName(), method.getParameterTypes().length, matcher.groupCount());

                            handleFailure(element, text, message);
                            throw new RuntimeException(message);
                        }
                    }
                }
            }

            handleFailure(element, text, "No @Spec() method matching: " + text);
            throw new RuntimeException("No @Spec() method matching: " + text);

        } catch (Throwable e) {
            handleFailure(element, e.getMessage(), e.getCause().toString());
            throw new RuntimeException(e);
        }
    }

    private Object findEvalSourceObject(Evaluator evaluator, Element element) {
        Object result;

        try {
            Field rootField = evaluator.getClass().getSuperclass().getDeclaredField("rootObject");
            rootField.setAccessible(true);
            result = rootField.get(evaluator);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }


    private void handleSuccess(Element element, String fact, String src) {
        Matcher matcher = Pattern.compile(src).matcher(fact);

        int index = 0;

        element.moveChildrenTo(new Element("null"));

        if (matcher.matches()) {

            for (int i = 1; i <= matcher.groupCount(); i++) {

                int start = matcher.start(i);
                int end = matcher.end(i);

                element.appendChild(newElement("span", "", fact.substring(index, start)));

                element.appendChild(newElement("b", "", matcher.group(i)));

                index = end;

            }
        }

        if (index < fact.length()) {
            element.appendChild(newElement("span", "", fact.substring(index)));
        }

        element.addStyleClass("success");
    }

    private void handleFailure(Element element, Map<Integer, SpecException.FactModifier> groupChanges, String fact, String src) {
        Matcher matcher = Pattern.compile(src).matcher(fact);

        int index = 0;

        element.moveChildrenTo(new Element("null"));

        if (matcher.matches()) {

            for (int i = 1; i <= matcher.groupCount(); i++) {

                int start = matcher.start(i);
                int end = matcher.end(i);

                element.appendChild(newElement("span", "", fact.substring(index, start)));

                if (groupChanges.containsKey(i)) {
                    groupChanges.get(i).modify(element, matcher.group(i));
                } else {
                    element.appendChild(newElement("b", "", matcher.group(i)));
                }

                index = end;

            }
        }

        if (index < fact.length()) {
            element.appendChild(newElement("span", "", fact.substring(index)));
        }

        element.addStyleClass("failure");
    }


    private void handleFailure(final Element element, final String fact, String message) {
        handleResult(element, "failure", fact, message);
    }

    private void handleResult(final Element element, final String style, final String fact, final String message) {
        element.moveChildrenTo(new Element("null"));
        element.appendChild(stringElement("", fact));
        element.appendChild(stringElement(style, message));
        element.addStyleClass(style);
    }

    private Element stringElement(final String style, String content) {
        return newElement("span", style, content);
    }

    private Element newElement(final String type, String style, String content) {
        Element evalTitle = new Element(type);
        evalTitle.appendText(content);
        evalTitle.addStyleClass(style);
        return evalTitle;
    }

    private Element extractResult(Object result) {
        Element exampleContent = new Element("p");
        exampleContent.appendText(result.toString());
        Element exampleContainer = new Element("div");
        exampleContainer.appendChild(exampleContent);
        exampleContainer.addStyleClass("example");
        return exampleContainer;
    }

}