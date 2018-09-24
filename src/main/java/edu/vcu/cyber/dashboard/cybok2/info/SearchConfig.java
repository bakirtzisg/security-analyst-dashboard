package edu.vcu.cyber.dashboard.cybok2.info;

public class SearchConfig
{
	public static SearchConfig searchConfig = new SearchConfig();

	private ConfigUse SEARCHED = new ConfigUse();
	private ConfigUse REPORTED = new ConfigUse();
	private ConfigWeightStuffs FAM_WTS = new ConfigWeightStuffs();

	private boolean TW; // text weighting
	private boolean UT; // use taxascore
	private boolean SDT; // singe(F)/Double(T) taxascore
	private String NAME;


	public void setSearchParams(boolean[] params)
	{
		SEARCHED.set(params);
	}

	public void setReportedParams(boolean[] params)
	{
		REPORTED.set(params);
	}

	public boolean[] getSearchParams()
	{
		return SEARCHED.get();
	}

	public boolean[] getReportedParams()
	{
		return REPORTED.get();
	}

	public String getName()
	{
		return NAME;
	}

	public boolean taxaWeighting()
	{
		return TW;
	}

	public boolean useTaxaScore()
	{
		return UT;
	}

	public boolean singleDoubleTaxa()
	{
		return SDT;
	}

	public double[][] getAllWeights()
	{
		return new double[][]{FAM_WTS.A.weights(), FAM_WTS.W.weights(), FAM_WTS.V.weights()};
	}

	private class ConfigUse
	{
		boolean A; // Attack
		boolean W; // Weakness
		boolean V; // Vulnerability

		boolean[] get()
		{
			return new boolean[]{A, W, V};
		}

		void set(boolean[] vals)
		{
			A = vals[0];
			W = vals[1];
			V = vals[2];
		}
	}

	private class ConfigWeightStuffs
	{
		ConfigWeights A = new ConfigWeights(); // Attack
		ConfigWeights W = new ConfigWeights(); // Weakness
		ConfigWeights V = new ConfigWeights(); // Vulnerability

		void set(double[][] weights)
		{
			A.set(weights[0]);
			W.set(weights[1]);
			V.set(weights[2]);
		}
	}

	private class ConfigWeights
	{
		double M; // Matched
		double A; // Anscestral
		double D; // Descendant
		double S; // Source

		double[] weights()
		{
			return new double[]{M, A, D, S};
		}

		void set(double[] weights)
		{
			M = weights[0];
			A = weights[1];
			D = weights[2];
			S = weights[3];
		}
	}

}
