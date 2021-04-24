/*
 *     Copyright 2021 Horstexplorer @ https://www.netbeacon.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.netbeacon.d43z.one.objects.base;

import de.netbeacon.d43z.one.objects.bp.IContentprovider;
import de.netbeacon.d43z.one.objects.bp.ITrigger;

import java.util.function.Function;

public abstract class Trigger<T> implements ITrigger {

    private final int pos;
    private final String desc;
    private final Type type;
    private final Function<String, T> trigger;
    private final Function<T, Boolean> toBool;
    private final Function<T, Float> toFloat;

    public Trigger(int pos, String desc, Type type, Function<String, T> trigger, Function<T, Boolean> toBool, Function<T, Float> toFloat) {
        this.pos = pos;
        this.desc = desc;
        this.type = type;
        this.trigger = trigger;
        this.toBool = toBool;
        this.toFloat = toFloat;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public boolean testB(String input) {
        return toBool.apply(trigger.apply(input));
    }

    @Override
    public float testF(String input) {
        return toFloat.apply(trigger.apply(input));
    }

    @Override
    public boolean testB(IContentprovider iContentprovider) {
        return testB(iContentprovider.getContent());
    }

    @Override
    public float testF(IContentprovider iContentprovider) {
        return testF(iContentprovider.getContent());
    }

    @Override
    public Type getType() {
        return type;
    }
}
