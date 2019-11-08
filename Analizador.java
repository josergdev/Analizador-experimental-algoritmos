import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Analizador {

	public static long milisToNanos(long milis) {
		return milis * 1000000;
	}

	public static long nanosToMilis(long nanos) {
		return nanos / 1000000;
	}

	public static Map<String, List<Long>> measureAlgorithm(int timeMeasure, long maxTime, double scaleFactor) {	
		Temporizador temp = new Temporizador(1);
		long globalTiempoPasado = 0;
		temp.iniciar();

		int n = 1;
		double nd = 1;

		List<Long> entradas = new ArrayList<>();
		List<Long> tiempos = new ArrayList<>();

		Temporizador tempAlg = new Temporizador(timeMeasure);
		while (maxTime > globalTiempoPasado) {
			if (Integer.MAX_VALUE <= (int) nd) break;
			tempAlg.iniciar();
			Algoritmo.f(n);
			tempAlg.parar();
			long tiempoPasado = tempAlg.tiempoPasado();
			tempAlg.reiniciar();
			entradas.add((long) n);
			tiempos.add(tiempoPasado);
			while (n == (int) nd) {
				nd *= scaleFactor;
			}
			n = (int) nd;

			globalTiempoPasado = temp.tiempoPasado();
		}

		Map<String,List<Long>> results = new HashMap<>();
		results.put("entradas", entradas);
		results.put("tiempos", tiempos);

		return results;
	}

	public static Map<String, List<Long>> measureAlgorithmWithEntries(int timeMeasure, List<Integer> entries) {		
		List<Long> entradas = new ArrayList<>();
		List<Long> tiempos = new ArrayList<>();

		Temporizador tempAlg = new Temporizador(timeMeasure);

		for (Integer entry : entries) {
			tempAlg.iniciar();
			Algoritmo.f(entry);
			tempAlg.parar();
			long tiempoPasado = tempAlg.tiempoPasado();
			tempAlg.reiniciar();
			entradas.add((long) entry);
			tiempos.add(tiempoPasado);
		}

		Map<String,List<Long>> results = new HashMap<>();
		results.put("entradas", entradas);
		results.put("tiempos", tiempos);

		return results;
	}

	public static List<Integer> longListToIntgerList(List<Long> listOfLongs) {
		List<Integer> listOfIntegers = new ArrayList<>();
		for (Long number : listOfLongs) {
			listOfIntegers.add(number.intValue());
		}
		return listOfIntegers;
	}
 
	public static int timesToReduce(int n, int max) {
		int reducer = (int) Math.pow(10,max);
		int times = 0;
		while (n >= reducer) {
			n *= 0.1;
			times += 1;
		}
		return times;
	}

	public static List<Integer> reduceListOfNumbers(List<Integer> listOfNumbers, int max) {
		int lastNumber = listOfNumbers.get(listOfNumbers.size()-1);
		int times = timesToReduce(lastNumber, max);
		int reducer = (int) Math.pow(10,times);

		List<Integer> reducedList = new ArrayList<>();
		for (Integer number : listOfNumbers) {
			int reducedNumber = number / reducer;
			if (reducedNumber == 0) {
				reducedList.add(1);
			} else {
				reducedList.add(number / reducer);
			}
		}
		return reducedList;
	}

	public static Map<String, List<BigDecimal>> theoricResults(List<Integer> entradas) {
		Map<String, List<BigDecimal>> results = new HashMap<>();
		List<BigDecimal> oneC = new ArrayList<>();
		for (Integer entrada : entradas) {
			oneC.add(BigDecimal.ONE);
		}
		results.put("1", oneC);

		List<BigDecimal> lognC = new ArrayList<>();
		for (Integer entrada : entradas) {
			double logn = Math.log((double) entrada) / Math.log(2);
			try {
				lognC.add(BigDecimal.valueOf(logn));
			} catch (Exception e) {
				System.out.println(logn);
			}
		}
		results.put("LOGN", lognC);

		List<BigDecimal> nC = new ArrayList<>();
		for (Integer entrada : entradas) {
			nC.add(BigDecimal.valueOf((double) entrada));
		}
		results.put("N", nC);

		List<BigDecimal> nlognC = new ArrayList<>();
		for (Integer entrada : entradas) {
			nlognC.add(BigDecimal.valueOf((double) entrada).multiply(BigDecimal.valueOf(Math.log((double) entrada))));
		}
		results.put("NLOGN", nlognC);

		List<BigDecimal> ntwoC = new ArrayList<>();
		for (Integer entrada : entradas) {
			BigDecimal b1 = BigDecimal.valueOf(entrada);
			BigDecimal b2 = BigDecimal.valueOf(entrada);
			ntwoC.add(b1.multiply(b2));
		}
		results.put("N2", ntwoC);

		List<BigDecimal> nthreeC = new ArrayList<>();
		for (Integer entrada : entradas) {
			BigDecimal b1 = BigDecimal.valueOf(entrada);
			BigDecimal b2 = BigDecimal.valueOf(entrada);
			BigDecimal b3 = BigDecimal.valueOf(entrada);
			nthreeC.add(b1.multiply(b2).multiply(b3));
		}
		results.put("N3", nthreeC);

		List<BigDecimal> twonC = new ArrayList<>();
		for (Integer entrada : entradas) {
			BigInteger n = BigInteger.valueOf(2);
			for (int i = 1; i < entrada; i++) {
				n = n.multiply(BigInteger.valueOf(2));
			}
			twonC.add(new BigDecimal(n));

		}
		results.put("2N", twonC);

		List<BigDecimal> nfC = new ArrayList<>();
		for (Integer entrada : entradas) {
			BigInteger n = BigInteger.valueOf(entrada);
			BigDecimal nd = new BigDecimal(factorial(n));
			nfC.add(nd);
		}
		results.put("NF", nfC);

		return results;
	}

	public static BigInteger factorial(BigInteger n) {
		if ( n == null ) {
			throw new IllegalArgumentException();
		}
		else if ( n.signum() == - 1 ) {
			// negative
			throw new IllegalArgumentException("Argument must be a non-negative integer");
		}
		else {
			BigInteger factorial = BigInteger.ONE;
		for ( BigInteger i = BigInteger.ONE; i.compareTo(n) < 1; i = i.add(BigInteger.ONE) ) {
			factorial = factorial.multiply(i);
		}
			return factorial;
		}
	}

	public static Map<String, List<BigDecimal>> compareResults(List<String> complejidades, List<Long> results, Map<String, List<BigDecimal>> theoricResults) {
		Map<String, List<BigDecimal>> comparedResults = new HashMap<>();
		for (String compl : complejidades) {
			List<BigDecimal> toCompare = theoricResults.get(compl);
			List<BigDecimal> compResults = new ArrayList<>();
			for (int i = 0; i < results.size(); i++) {
				if (toCompare.get(i).doubleValue() != BigDecimal.ZERO.doubleValue()) {
					BigDecimal n = BigDecimal.valueOf(results.get(i)).divide(toCompare.get(i), 2, RoundingMode.HALF_UP);
					compResults.add(n);
				} else {
					compResults.add(BigDecimal.valueOf(results.get(i)));
				}
			}
			comparedResults.put(compl, compResults);
		}
		return comparedResults;
	}

	public static BigDecimal variance(List<BigDecimal> list) {
		BigDecimal mean = BigDecimal.ZERO;
		for (int i = 0; i < list.size(); i++) {
			mean = mean.add(list.get(i));
		}
		mean = mean.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);

		BigDecimal variance = BigDecimal.ZERO;
		for (int i = 0; i < list.size(); i++) {
			variance = variance.add((list.get(i).subtract(mean)).multiply(list.get(i).subtract(mean)));
		}
		variance = variance.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
		return variance;
	}

	public static Map<String, BigDecimal> varianceOfComparedResults(Map<String, List<BigDecimal>> comparedResults) {
		Map<String, BigDecimal> result = new HashMap<>();

		for (Map.Entry<String, List<BigDecimal>> comp : comparedResults.entrySet()) {
			List<BigDecimal> values = comp.getValue();
			BigDecimal varComp = variance(values);
			result.put(comp.getKey(), varComp);
		}

		return result;
	}

	public static Map<String,List<Long>> lastResultsOfResults(Map<String,List<Long>> results, int lasts) {

		Map<String,List<Long>> lastResults = new HashMap<>();

		if (results.get("entradas").size() - lasts < 0) {
			lastResults.put("entradas", results.get("entradas"));
			lastResults.put("tiempos", results.get("tiempos"));
		} else {
			for (Entry<String,List<Long>> entry : results.entrySet()) {
				lastResults.put(entry.getKey(), entry.getValue().subList(entry.getValue().size() - lasts, entry.getValue().size()));
			}
		}

		return lastResults;
	}

	public static Map<String, BigDecimal> analyzeComparedResults(Map<String, List<BigDecimal>> comparedResults) {
		Map<String,BigDecimal> analyzedResults = new HashMap<>();

		for (Entry<String, List<BigDecimal>> entry : comparedResults.entrySet()) {
			List<BigDecimal> entryList = entry.getValue();
			BigDecimal last = entryList.get(entryList.size()-1);
			entryList.remove(entryList.size()-1);

			List<BigDecimal> analyzedList = new ArrayList<>();
			for (BigDecimal entryBd : entryList) {
				if (entryBd.doubleValue() == BigDecimal.ZERO.doubleValue()) {
					analyzedList.add(BigDecimal.ZERO);
				} else {
					analyzedList.add(last.divide(entryBd, 2, RoundingMode.HALF_UP));
				}
			}

			BigDecimal analyzedListSum = BigDecimal.ZERO;
			for (BigDecimal analyzedTime : analyzedList) {
				analyzedListSum = analyzedListSum.add(analyzedTime);
			}

			BigDecimal avg = analyzedListSum.divide(BigDecimal.valueOf(analyzedList.size()), 2, RoundingMode.HALF_UP);
			analyzedResults.put(entry.getKey(), avg);
		}

		return analyzedResults;
	}

	public static void printAnalyzedComplexity(List<String> complejidades, Map<String,BigDecimal> analyzedResults) {
		BigDecimal result = null;
		String compl = "";

		for (String comp : complejidades) {
			BigDecimal num = BigDecimal.ONE.subtract(analyzedResults.get(comp)).abs();
			if (result == null) {
				result = num;
				compl = comp;
			} else {
				if (result.compareTo(num) == 1) {
					result = num;
					compl = comp;
				}
			}
		}

		System.out.println(compl);
	}
 
	public static void main(String arg[]) {	

		String argAction = arg.length == 0 ? "" : arg[0];
		Boolean plot = argAction.equals("plot") ? true : false;
		Boolean verbose = argAction.equals("verbose") ? true : false;
		// Boolean verbose = true;

		List<String> complejidades = Arrays.asList("1", "LOGN", "N", "NLOGN", "N2", "N3", "2N", "NF");

		int timeMeasure = 2;
		int maxTime = 2000;
		double scaleFactor = 1.5;
		int lasts = 100;
		int entriesValueReduction = 5;

		if (plot) {

			Map<String,List<Long>> results = measureAlgorithm(timeMeasure, maxTime, scaleFactor);
			List<Integer> entradas = longListToIntgerList(results.get("entradas"));
			List<Long> tiempos = results.get("tiempos");
			
			for (int i = 0; i < entradas.size(); i++) {
				System.out.println(entradas.get(i) + "," + tiempos.get(i));
			}

		} else if (verbose) {

			System.out.println("Midiendo algoritmo escalando entradas");
			Map<String,List<Long>> results = measureAlgorithm(timeMeasure, maxTime, scaleFactor);
			Map<String,List<Long>> lastResults = lastResultsOfResults(results, lasts);

			System.out.println("Ultimas entradas: " + lastResults.get("entradas"));
			System.out.println("Ultimos tiempos: " + lastResults.get("tiempos"));
			System.out.println("--------");


			List<Integer> ultimasEntradas = longListToIntgerList(lastResults.get("entradas"));
			List<Integer> entradasReducidas = reduceListOfNumbers(ultimasEntradas, entriesValueReduction);

			System.out.println("Entradas reducidas: " + entradasReducidas);
			System.out.println("Ultimos tiempos: " + lastResults.get("tiempos"));
			System.out.println("--------");

			System.out.println("Calculando resultados teoricos");
			Map<String, List<BigDecimal>> resultadosTeoricos = theoricResults(entradasReducidas);
			System.out.println("Comparando con expeimentales");
			Map<String, List<BigDecimal>> resultadosComparados = compareResults(complejidades, lastResults.get("tiempos"), resultadosTeoricos);

			for (String compl : complejidades) {
				System.out.print(compl + " -> ");
				resultadosComparados.get(compl).forEach(result -> System.out.print(result + " "));
				System.out.println();
			}
			System.out.println("--------");

			Map<String,BigDecimal> resultadosAnalizados = analyzeComparedResults(resultadosComparados);

			for (String compl : complejidades) {
				System.out.println(compl + " -> " + resultadosAnalizados.get(compl));
			}
			System.out.println("--------");

			printAnalyzedComplexity(complejidades, resultadosAnalizados);

		} else {
			Map<String,List<Long>> results = measureAlgorithm(timeMeasure, maxTime, scaleFactor);
			Map<String,List<Long>> lastResults = lastResultsOfResults(results, lasts);

			List<Integer> ultimasEntradas = longListToIntgerList(lastResults.get("entradas"));
			List<Integer> entradasReducidas = reduceListOfNumbers(ultimasEntradas, entriesValueReduction);

			Map<String, List<BigDecimal>> resultadosTeoricos = theoricResults(entradasReducidas);
			Map<String, List<BigDecimal>> resultadosComparados = compareResults(complejidades, lastResults.get("tiempos"), resultadosTeoricos);

			Map<String,BigDecimal> resultadosAnalizados = analyzeComparedResults(resultadosComparados);
			printAnalyzedComplexity(complejidades,resultadosAnalizados);
		}
	}
}
