package service.annotationsetup;

import service.configuration.JadeAgent;

import java.lang.annotation.Annotation;


public interface SetAnnotationNumber {
    default void setNumber(int number){
        try {
            final JadeAgent oldAnnotation = (JadeAgent) getClass().getAnnotations()[0];
            Annotation newAnnotation = new JadeAgent() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return oldAnnotation.annotationType();
                }
                @Override
                public int number() {
                    return number;
                }
                @Override
                public String value() {
                    return "";
                }
            };
            AnnotationHelper.alterAnnotationOn(getClass(), JadeAgent.class,newAnnotation);
        } catch (Exception ex){
            System.out.println("Can't set number of agents.");
        }
    }

}
