package me.contaria.standardsettings.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.contaria.standardsettings.StandardGameOptions;
import me.contaria.standardsettings.StandardSettings;
import me.contaria.standardsettings.mixin.accessors.OptionAccessor;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class CyclingOptionStandardSetting extends StandardSetting<Integer> {
    private final CyclingOption option;
    private final ToIntFunction<GameOptions> optionGetter;

    public CyclingOptionStandardSetting(String id, @Nullable String category, StandardGameOptions options, CyclingOption option, ToIntFunction<GameOptions> optionGetter) {
        super(id, category, options);
        this.option = option;
        this.optionGetter = optionGetter;

        this.set(this.getOption());
    }

    @Override
    public Integer get(GameOptions options) {
        return this.optionGetter.applyAsInt(options);
    }

    @Override
    public void set(GameOptions options, Integer value) {
        int original = this.get(options);
        int current = original;
        while (current != value) {
            this.option.cycle(options, 1);
            current = this.get(options);

            if (current == original) {
                StandardSettings.LOGGER.warn("Failed to set {} to {}.", this.getID(), value);
                break;
            }
        }
    }

    @Override
    protected void valueFromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsInt());
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
        return StandardSetting.getTextWithoutPrefix(this.option.getMessage(this.options), this.option.getDisplayPrefix());
    }

    @Override
    public @NotNull AbstractButtonWidget createMainWidget() {
        return new OptionButtonWidget(0, 0, 120, 20, this.option, this.getText(), button -> {
            this.option.cycle(this.options, 1);
            button.setMessage(this.getText());
        });
    }
}
