/*
 *     Copyright 2020 Horstexplorer @ https://www.netbeacon.de
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

package de.netbeacon.d43z.one.settings;

import de.netbeacon.d43z.one.objects.bp.ISimilarity;

import java.util.function.Supplier;

public class StaticSettings {

    public static final Setting<Integer> CONTENT_SHARD_SIZE = new Setting<>(5000);
    public static final Setting<Integer> BUFFER_MAX_SIZE = new Setting<>(5);
    public static final Setting<Boolean> EVAL_ENABLE_BUFFER_BONUS_POLICY = new Setting<>(true);
    public static final Setting<Float> BUFFER_BONUS = new Setting<>(0.05F);
    public static final Setting<Float> BUFFER_BONUS_SUBTRACTION = new Setting<>(0.005F);
    public static final Setting<Boolean> EVAL_ENABLE_TAG_POLICY = new Setting<>(false);
    public static final Setting<Float> EVAL_TAG_BONUS_PER_MATCH = new Setting<>( 0.05F);
    public static final Setting<Float> EVAL_TAG_POLICY_OVERRIDE_THRESHOLD = new Setting<>(0.49F);
    public static final Setting<Integer> EVAL_LIAMUS_JACCARD_NGRAM = new Setting<>(2);
    public static final Setting<Boolean> EVAL_LIAMUS_JACCARD_LOWERCASE_MATCH = new Setting<>(true);
    public static final Setting<Float> EVAL_RANDOM_DIF = new Setting<>(0.00005F);
    public static final Setting<Integer> EVAL_MAX_PROCESSING_THREADS = new Setting<>(() -> Runtime.getRuntime().availableProcessors()*2);
    public static final Setting<Integer> EVAL_MAX_THREADS_PER_REQUEST = new Setting<>(() -> EVAL_MAX_PROCESSING_THREADS.get()/8);
    public static final Setting<Integer> EVAL_MAX_CONCURRENT_TASKS = new Setting<>(() -> EVAL_MAX_PROCESSING_THREADS.get()/EVAL_MAX_THREADS_PER_REQUEST.get());
    public static final Setting<ISimilarity.Algorithm> EVAL_ALGORITHM = new Setting<>(ISimilarity.Algorithm.LIAMUS_JACCARD);
    public static final Setting<Integer> EVAL_AVG_BASE = new Setting<>(250);
    public static final Setting<Integer> EVAL_MAX_PROCESSING_TIME = new Setting<>(5000);
    public static final Setting<Integer> EVAL_MIN_PROCESSING_TIME = new Setting<>(250);


    public static class Setting<T> {

        private Supplier<T> supplier;

        public Setting(T value){
            this.supplier = () -> value;
        }

        public Setting(Supplier<T> supplier){
            this.supplier = supplier;
        }

        public void override(T value){
            this.supplier = () -> value;
        }

        public void override(Supplier<T> supplier){
            this.supplier = supplier;
        }

        public T get(){
            return supplier.get();
        }

    }
}
