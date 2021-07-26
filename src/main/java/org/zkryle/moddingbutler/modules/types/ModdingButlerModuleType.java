package org.zkryle.moddingbutler.modules.types;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;
import org.jetbrains.annotations.NotNull;
import org.zkryle.moddingbutler.icons.ModdingButlerIcons;
import org.zkryle.moddingbutler.modules.builders.ModdingButlerModuleBuilder;

import javax.swing.*;

public class ModdingButlerModuleType extends ModuleType<ModdingButlerModuleBuilder>{
    private static final String ID = "MODDINGBUTLER_MODULE_TYPE";

    public ModdingButlerModuleType() {
        super(ID);
    }

    public static ModdingButlerModuleType getInstance() {
        return (ModdingButlerModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public ModdingButlerModuleBuilder createModuleBuilder() {
        return new ModdingButlerModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Modding Butler";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "A forge/fabric enviroment setup for Intellij/Eclipse";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return ModdingButlerIcons.ModdingButlerIcon;
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps( @NotNull WizardContext wizardContext,
                                                 @NotNull ModdingButlerModuleBuilder moduleBuilder,
                                                 @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }

}
