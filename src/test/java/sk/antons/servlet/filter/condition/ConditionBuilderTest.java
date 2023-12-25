/*
 * Copyright 2018 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.servlet.filter.condition;


import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class ConditionBuilderTest {
	private static Logger log = Logger.getLogger(ConditionBuilderTest.class.getName());


    @Test
	public void parseTest() throws Exception {
        Condition<String> conditon = ConditionBuilder.instance(String.class)
            .add(StringCondition.instance(StringCondition.Operation.STARTS_WITH, "po", s -> s))
            .and()
            .lb()
                .add(s -> s.length() > 1000)
                .or()
                .add(s -> s.length() < 10)
            .rb()
            .condition();
        System.out.println(" ---- " + conditon);
        Assert.assertNotNull(conditon);
        Assert.assertTrue(conditon.check("pokus"));
        Assert.assertFalse(conditon.check("pokuspokus"));
    }

}
