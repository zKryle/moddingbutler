package org.zkryle.moddingbutler.modules.builders;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.zkryle.moddingbutler.modules.types.ModdingButlerModuleType;
import org.zkryle.moddingbutler.modules.wizardsteps.ModdingButlerModuleWizardStep;

public class ModdingButlerModuleBuilder extends ModuleBuilder{

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
    }

    @Override
    public ModdingButlerModuleType getModuleType() {
        return ModdingButlerModuleType.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep( WizardContext context, Disposable parentDisposable) {
        return new ModdingButlerModuleWizardStep();
    }

}
