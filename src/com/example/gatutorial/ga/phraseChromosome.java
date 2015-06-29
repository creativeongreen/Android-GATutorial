/*
 * Copyright (C) 2015 creativeongreen
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gatutorial.ga;

/**
*
* @author creativeongreen
* 
* Chromosome implementation
* 
*/
public class phraseChromosome {

	char[] genes;
	float fitness;

	public phraseChromosome(int numGenes) {
		genes = new char[numGenes];
		for (int i = 0; i < genes.length; i++) {
			genes[i] = getRandomGene();
		}
	}

	// calculate fitness with right char at the right position
	public void calcFitness(String target) {
		int score = 0;

		for (int i = 0; i < genes.length; i++) {
			if (genes[i] == target.charAt(i)) {
				score++;
			}
		}

		fitness = (float) score / target.length();
		// fitness = (float) Math.pow(score, 2.0);
	}

	public phraseChromosome crossover(phraseChromosome partner) {
		phraseChromosome child = new phraseChromosome(genes.length);
		int midpoint = (int) (genes.length * Math.random());

		for (int i = 0; i < genes.length; i++) {
			if (i < midpoint)
				child.genes[i] = genes[i];
			else
				child.genes[i] = partner.genes[i];
		}

		return child;
	}

	public void mutate(float mutationRate) {
		for (int i = 0; i < genes.length; i++) {
			if (Math.random() <= mutationRate) {
				genes[i] = getRandomGene();
			}
		}
	}

	public float getFitness() {
		return fitness;
	}

	public float maxFitness() {
		return (float) 1.0;
	}

	public String getPhrase() {
		return new String(genes);
	}

	private char getRandomGene() {
		return (char) (94 * Math.random() + 32); // ascii-code decimal: 32 ~ 126
	}

}
