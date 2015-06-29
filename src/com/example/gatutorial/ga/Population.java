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
* Population implementation
* 
*/
public class Population {

	phraseChromosome[] phraseChromosomes;

	public Population(int numOfDNAs, int sizeDNA) {
		phraseChromosomes = new phraseChromosome[numOfDNAs];
		// Initialize population
		// Loop and create individuals
		for (int i = 0; i < size(); i++) {
			phraseChromosome newObjectDNA = new phraseChromosome(sizeDNA);
			setObjectDNA(i, newObjectDNA);
		}
	}

	// get individual
	public phraseChromosome getObjectDNA(int index) {
		return phraseChromosomes[index];
	}

	// set individual
	public void setObjectDNA(int index, phraseChromosome indiv) {
		phraseChromosomes[index] = indiv;
	}

	public void calcDNAFitness(String target) {
		for (int i = 0; i < size(); i++)
			phraseChromosomes[i].calcFitness(target);
	}

	public float calcTotalFitness() {
		float f = 0.0f;

		for (int i = 0; i < size(); i++)
			f += phraseChromosomes[i].getFitness();

		return f;
	}

	public boolean checkIfFit() {
		for (int i = 0; i < size(); i++)
			if (phraseChromosomes[i].getFitness() == 1.0f) {
				return true;
			}

		return false;
	}

	// Get population size
	public int size() {
		return phraseChromosomes.length;
	}

}
