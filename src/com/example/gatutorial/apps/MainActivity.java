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

package com.example.gatutorial.apps;

import java.util.ArrayList;

import com.example.gatutorial.R;
import com.example.gatutorial.R.id;
import com.example.gatutorial.R.layout;
import com.example.gatutorial.R.menu;
import com.example.gatutorial.ga.Population;
import com.example.gatutorial.ga.phraseChromosome;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

/**
*
* @author creativeongreen
* 
* MainActivity
* 
*/
public class MainActivity extends ActionBarActivity {

	private static final String LOG_TAG = "GA_MainActivity";

	private static boolean keepRunning;
	private static Thread tEvolvePopulation;
	private static long numOfGenerations = 0;
	private static long timeEvolveStart, timeEvolveDiff;
	private static Handler hTimer;
	private static Runnable rTimelapseTask;
	private static TextView tvTestResults;
	private static EditText etInputPhrase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onPause() {
		// Log.d(LOG_TAG, "onPause()");

		if (tEvolvePopulation != null) {
			keepRunning = false;
			hTimer.removeCallbacks(rTimelapseTask);
			tEvolvePopulation.interrupt();
			tEvolvePopulation = null;
		}

		// place super pause at last
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Log.d(LOG_TAG, "PlaceholderFragment/onCreateView");
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			etInputPhrase = (EditText) rootView.findViewById(R.id.et_1);
			tvTestResults = (TextView) rootView.findViewById(R.id.test_results);
			return rootView;
		}
	} // /class PlaceholderFragment

	public void onClickStartEvolve(View v) {
		// Shakespeare's plays example
		int totalPopulation = 500;
		final String target;
		final float mutationRate = 0.01f;
		final Population population;
		final ArrayList<phraseChromosome> matingPool = new ArrayList<phraseChromosome>();
		keepRunning = true;
		numOfGenerations = 0;

		target = etInputPhrase.getText().toString();
		if (target.length() == 0)
			return;
		tvTestResults.setText("Evolving phrase- " + target + "\n");

		// Step 1: Initialize population
		population = new Population(totalPopulation, target.length());

		tEvolvePopulation = new Thread(new Runnable() {
			@Override
			public void run() {
				while (keepRunning) {
					// Step 2: Selection
					// Step 2a: Calculate fitness
					population.calcDNAFitness(target);
					float totalFitness = 0.0f;

					if (population.checkIfFit()) {
						keepRunning = false;
					} else {
						totalFitness = population.calcTotalFitness();

						// Step 2b: Build mating pool - using Roulette Wheel Selection
						// Parents are selected according to their fitness. The better the
						// chromosomes are, the more chances to be selected they have.
						// Chromosome with bigger fitness will be selected more times.
						for (int i = 0; i < population.size(); i++) {
							// Add each member n times according to its fitness score probability.
							int n = (int) (population.getObjectDNA(i)
									.getFitness() / totalFitness * population
									.size());
							for (int j = 0; j < n; j++) {
								try {
									matingPool.add(population.getObjectDNA(i));
								} catch (Throwable e) {
									Log.d(LOG_TAG,
											"do System.gc(): " + e.getMessage());
									System.gc();
									keepRunning = false;
									break;
								}
							}
						}

						// Step 3: Reproduction
						for (int i = 0; i < population.size(); i++) {
							int a = (int) (matingPool.size() * Math.random());
							int b = (int) (matingPool.size() * Math.random());
							phraseChromosome partnerA = matingPool.get(a);
							phraseChromosome partnerB = matingPool.get(b);

							// Step 3a: Crossover
							phraseChromosome child = partnerA.crossover(partnerB);

							// Step 3b: Mutation
							child.mutate(mutationRate);

							population.setObjectDNA(i, child);
						}

						matingPool.clear();
						// System.gc();
						numOfGenerations++;
					}

				} // /while loop

				timeEvolveDiff = System.currentTimeMillis() - timeEvolveStart;

				try {
				} catch (Throwable e) {
					// system will throw an exception, don't care it
					// Called FromWrongThreadException: only the original thread that created a view
					// hierarchy can touch its views
				}

			} // /run()
		}); // /Thread()

		hTimer = new Handler();
		rTimelapseTask = new Runnable() {
			public void run() {
				if (keepRunning) {
					tvTestResults.append(numOfGenerations + "- "
							+ population.getObjectDNA(0).getPhrase() + "\n");
					hTimer.postDelayed(rTimelapseTask, 3000);
				} else {
					tvTestResults.append("--- Solution found! ---\nTimelapse= "
							+ timeEvolveDiff
							+ " mills\nNumber of generations= "
							+ numOfGenerations + "\n");
				}
			}
		};

		hTimer.post(rTimelapseTask);

		timeEvolveStart = System.currentTimeMillis();
		tEvolvePopulation.start();

	} // /onClickStartEvolve()

}
