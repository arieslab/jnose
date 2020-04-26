package br.ufba.jnose.pages;

import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;

public class ConfigPage extends BasePage {
    private static final long serialVersionUID = 1L;


    private Boolean assertionRoulette;
    private Boolean conditionalTestLogic;
    private Boolean constructorInitialization;
    private Boolean defaultTest;
    private Boolean dependentTest;
    private Boolean duplicateAssert;
    private Boolean eagerTest;

    public ConfigPage() {

        assertionRoulette = TestSmellDetector.assertionRoulette;
        conditionalTestLogic = TestSmellDetector.conditionalTestLogic;
        constructorInitialization = TestSmellDetector.constructorInitialization;
        defaultTest = TestSmellDetector.defaultTest;
        dependentTest = TestSmellDetector.dependentTest;
        duplicateAssert = TestSmellDetector.duplicateAssert;
        eagerTest = TestSmellDetector.eagerTest;

        Form form = new Form<String>("form"){
            @Override
            protected void onSubmit() {
                System.out.println(assertionRoulette);
                System.out.println(conditionalTestLogic);
                System.out.println(constructorInitialization);
                System.out.println(defaultTest);
                System.out.println(dependentTest);
                System.out.println(duplicateAssert);
                System.out.println(eagerTest);

                TestSmellDetector.assertionRoulette = assertionRoulette;
                TestSmellDetector.conditionalTestLogic = conditionalTestLogic;
                TestSmellDetector.constructorInitialization = constructorInitialization;
                TestSmellDetector.defaultTest = defaultTest;
                TestSmellDetector.dependentTest = dependentTest;
                TestSmellDetector.duplicateAssert = duplicateAssert;
                TestSmellDetector.eagerTest = eagerTest;

            }
        };

        form.add(new CheckBox("cbAssertionRoulette", new PropertyModel<>(this, "assertionRoulette")));
        form.add(new CheckBox("cbConditionalTestLogic", new PropertyModel<>(this, "conditionalTestLogic")));
        form.add(new CheckBox("cbConstructorInitialization", new PropertyModel<>(this, "constructorInitialization")));
        form.add(new CheckBox("cbDefaultTest", new PropertyModel<>(this, "defaultTest")));
        form.add(new CheckBox("cbDependentTest", new PropertyModel<>(this, "dependentTest")));
        form.add(new CheckBox("cbDuplicateAssert", new PropertyModel<>(this, "duplicateAssert")));
        form.add(new CheckBox("cbEagerTest", new PropertyModel<>(this, "eagerTest")));

        add(form);

    }
}