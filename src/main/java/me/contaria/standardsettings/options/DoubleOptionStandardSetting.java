package me.contaria.standardsettings.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.contaria.standardsettings.StandardGameOptions;
import me.contaria.standardsettings.mixin.accessors.DoubleOptionAccessor;
import me.contaria.standardsettings.mixin.accessors.OptionAccessor;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleOptionStandardSetting extends StandardSetting<Double> {
    private final DoubleOption option;

    public DoubleOptionStandardSetting(String id, @Nullable String category, StandardGameOptions options, DoubleOption option) {
        super(id, category, options);
        this.option = option;

        this.set(this.getOption());
    }

    @Override
    public Double get(GameOptions options) {
        return this.option.get(options);
    }

    @Override
    public void set(GameOptions options, Double value) {
        this.option.set(options, ((DoubleOptionAccessor) this.option).standardsettings$adjust(value));
    }

    @Override
    protected void valueFromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsDouble());
    }

    @Override
    protected JsonElement valueToJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public @NotNull String getName() {
        return I18n.translate(((OptionAccessor) this.option).standardsettings$getKey());
    }

    @Override
    public @NotNull String getDisplayText() {
        return StandardSetting.getTextWithoutPrefix(this.option.getDisplayString(this.options), this.option.getDisplayPrefix());
    }

    @Override
    public @NotNull AbstractButtonWidget createMainWidget() {
        return new DoubleOptionSliderWidget(this.options, 0, 0, 120, 20, this.option) {
            @Override
            protected void updateMessage() {
                this.setMessage(DoubleOptionStandardSetting.this.getText());
            }
        };
    }
}
