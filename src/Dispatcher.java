import java.io.File;
import java.util.*;

import static java.lang.System.exit;

class Dispatcher{

	static int second = 0, start = 0, time = 0;
	static boolean start_at_zero = true;

	public static void main(String[] args){
		IOCommandes ecran = new IOCommandes();

		while (true) {
			/* Fichier */
			System.out.println("Veuillez choisir un fichier a ordonnancer: ");
			System.out.println("processus_1.txt\t\tprocessus_2.txt\t\tprocessus_TD.txt");

			String fileName = ecran.lireEcran();
			String filePath = "files/" + fileName;
			File file = new File(filePath);

			if (!file.exists()) {
				System.out.println("Le fichier spécifié n'existe pas.");
				continue;
			}

			String lireFile = ecran.lireFile(file);
			Processus[] processus = ecran.tableProcess(lireFile);

			/* Ordonnecement*/
			System.out.println("Veuillez choisir une politique d'ordonnecement: ");
			System.out.println("1: FIFO (First In First Out)");
			System.out.println("2: FIFO Priorité");
			System.out.println("3: Round Robin");
			System.out.println("4: Round Robin Prio");
			System.out.println("5: FiFo Priorité avec préemption");
			System.out.println("6: Round Robin préemption");
			System.out.println("7: Round Robin Priorité préemption");
			System.out.println("0: Quitter");

			ArrayList processus_list = new ArrayList<>();

			switch (ecran.lireEcran()) {
				case "1":
					System.out.println("--------FIFO policy--------");
					/* FIFO (sans I/O, non-préantif et sans priorité) */

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					FIFObasic(processus_list, processus);

					break;
				case "2":
					System.out.println("--------FIFO Priorité--------");
					/* FIFO (sans I/O, non-préantif et avec priorité) */

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					FIFOPrio(processus_list, processus);

					break;
				case "3":
					System.out.println("--------Round Robin--------");
					System.out.println("WIP");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					Robin(processus_list, processus);

					break;
				case "4":
					System.out.println("--------Round Robin Priorité--------");
					System.out.println("WIP");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					RobinPrio(processus_list, processus);

					break;

				case "5":
					System.out.println("--------FIFO Priorité Préemption--------");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					FIFOPrioPremption(processus_list, processus);

					break;

				case "6":
					System.out.println("--------Rand Robin Préemption--------");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					RandRobinPreemption(processus_list, processus);

					break;

				case "7":
					System.out.println("--------Rand Robin Priorité Préemption--------");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					RandRobinPrioPreemption(processus_list, processus);

					break;

				case "8":
					System.out.println("--------SJF--------");

					for (int i = 0; i < processus.length; i++) {
						processus_list.add(processus[i]);
					}

					SJF(processus_list, processus);

					break;

				case "0":
					exit(0);
					break;
			}
		}
	}

	private static void SJF(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;
		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivateSJF(processus, start);

			time = start + 10;

			for(; start < time; start++){

				if(processToActivate != null){
					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.getExit_time() == processToActivate.getTotal_time()){
						start++;
						processToActivate.setBlocked(true);
						processToActivate.setBlockedTimeEnd(start);
						break;
					}

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						break;
					}
				}
			}
			unBlockProcess(processus);
			printState(processusList, start);

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}
	private static void RandRobinPrioPreemption(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;

		List<Processus> processReadyToActivate = new ArrayList<>();

		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivateRobinPrio(processus, processReadyToActivate, start);

			time = start + 10;
			boolean evenementDetect = false;

			for(; start < time; start++){

				if(processToActivate != null){

					for (Processus process : processus){
						if(process.getArrive_time() == start){
							evenementDetect = true;

							unBlockProcess(processus);
							printState(processusList, start);

							break;
						}
					}

					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.getExit_time() == processToActivate.getTotal_time()){
						start++;
						processToActivate.setBlocked(true);
						processToActivate.setBlockedTimeEnd(start);
						break;
					}

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						processReadyToActivate.remove(0);
						break;
					}
				}
			}

			if(!evenementDetect){
				unBlockProcess(processus);
				moveFirstProcessToEnd(processReadyToActivate);
				printState(processusList, start);
			}

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void RandRobinPreemption(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;

		List<Processus> processReadyToActivate = new ArrayList<>();

		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivateRobin(processus, processReadyToActivate, start);

			time = start + 10;
			boolean evenementDetect = false;

			for(; start < time; start++){

				if(processToActivate != null){

					for (Processus process : processus){
						if(process.getArrive_time() == start){
							evenementDetect = true;

							unBlockProcess(processus);
							printState(processusList, start);

							break;
						}
					}

					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						processReadyToActivate.remove(0);
						break;
					}
				}
			}

			if(!evenementDetect){
				moveFirstProcessToEnd(processReadyToActivate);
				printState(processusList, start);
			}


			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void FIFOPrioPremption(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;
		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivatePrio(processus, start);

			time = start + 10;
			boolean evenementDetect = false;

			for(; start < time; start++){

				if(processToActivate != null){

					for (Processus process : processus){
						if(process.getArrive_time() == start){
							evenementDetect = true;

							unBlockProcess(processus);
							printState(processusList, start);

							break;
						}
					}

					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.getExit_time() == processToActivate.getTotal_time()){
						start++;
						processToActivate.setBlocked(true);
						processToActivate.setBlockedTimeEnd(start);
						break;
					}

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						break;
					}

				}
			}

			if(!evenementDetect){
				unBlockProcess(processus);
				printState(processusList, start);
			}

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void Robin(ArrayList<Processus> processusList, Processus[] processus) {
		start = 0;
		time = 0;

		List<Processus> processReadyToActivate = new ArrayList<>();

		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivateRobin(processus, processReadyToActivate, start);

			time = start + 10;

			for(; start < time; start++){

				if(processToActivate != null){
					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						processReadyToActivate.remove(0);
						break;
					}
				}
			}
			moveFirstProcessToEnd(processReadyToActivate);
			printState(processusList, start);

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void RobinPrio(ArrayList<Processus> processusList, Processus[] processus) {
		start = 0;
		time = 0;

		List<Processus> processReadyToActivate = new ArrayList<>();

		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivateRobinPrio(processus, processReadyToActivate, start);

			time = start + 10;

			for(; start < time; start++){

				if(processToActivate != null){
					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.getExit_time() == processToActivate.getTotal_time()){
						start++;
						processToActivate.setBlocked(true);
						processToActivate.setBlockedTimeEnd(start);
						break;
					}

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						processReadyToActivate.remove(0);
						break;
					}
				}
			}
			unBlockProcess(processus);
			moveFirstProcessToEnd(processReadyToActivate);
			printState(processusList, start);

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	public static void FIFObasic(ArrayList processus_list, Processus[] processus){
		start = 0;
		time = 0;
		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivate(processus, start);

			time = start + 10;

			for(; start < time; start++){

				if(processToActivate != null){
					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						break;
					}
				}
			}
			printState(processus_list, start);
			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void FIFOPrio(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;
		while (!allProcessesFinished(processus)){

			setProcessToArrived(processus);
			Processus processToActivate = findProcessToActivatePrio(processus, start);

			time = start + 10;

			for(; start < time; start++){

				if(processToActivate != null){
					processToActivate.remain_time--;
					processToActivate.total_time++;
					processToActivate.setActif(true);

					if(processToActivate.getExit_time() == processToActivate.getTotal_time()){
						start++;
						processToActivate.setBlocked(true);
						processToActivate.setBlockedTimeEnd(start);
						break;
					}

					if(processToActivate.remain_time == 0){
						start++;
						processToActivate.setFinished(true);
						break;
					}
				}
			}
			unBlockProcess(processus);
			printState(processusList, start);

			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void unBlockProcess(Processus[] processus) {
		for (int i = 0; i < processus.length; i++){
			if(processus[i].isArrived() & !processus[i].isFinished() & processus[i].blocked & processus[i].getBlockedTimeEnd() <= start ){
				processus[i].setBlocked(false);
			}
		}
	}

	private static void setProcessToArrived(Processus[] processus) {
		for(int i = 0; i < processus.length; i++){
			if(processus[i].isArrived() != true & processus[i].isFinished() != true & processus[i].getArrive_time() <= start ){
				processus[i].setArrived(true);
			}
		}
	}

	public static Processus findProcessToActivate(Processus[] processus, int start){
		Processus[] processReadyToActivate = new Processus[processus.length];

		int count = 0;
		for(int i = 0; i < processus.length; i++){
			if(processus[i].getArrive_time() <= start & processus[i].isBlocked() != true & processus[i].isArrived() == true & processus[i].isFinished() != true){
				processReadyToActivate[count++] = processus[i];
			}
		}

		if (count == 0) {
			return null; // Return null if no process is arrived
		}

		processReadyToActivate = Arrays.copyOf(processReadyToActivate, count);

		// Tri par arrive time et après par nom
		Arrays.sort(processReadyToActivate, Comparator
				.comparing(Processus::getArrive_time)
				.thenComparing(Processus::getNameProcessus));

		return processReadyToActivate[0];
	}

	public static Processus findProcessToActivatePrio(Processus[] processus, int start){
		Processus[] processReadyToActivate = new Processus[processus.length];

		int count = 0;
		for(int i = 0; i < processus.length; i++){
			if(processus[i].getArrive_time() <= start & !processus[i].isBlocked() & processus[i].isArrived() & !processus[i].isFinished()){
				processReadyToActivate[count++] = processus[i];
			}
		}

		if (count == 0) {
			return null; // Return null if no process is arrived
		}

		processReadyToActivate = Arrays.copyOf(processReadyToActivate, count);

		// Tri par arrive time et après par nom
		Arrays.sort(processReadyToActivate, Comparator
				.comparing(Processus::getPriority_level)
				.thenComparing(Processus::getArrive_time)
				.thenComparing(Processus::getNameProcessus));

		return processReadyToActivate[0];
	}

	public static Processus findProcessToActivateRobin(Processus[] processus, List<Processus> processReadyToActivate, int start) {

		for (Processus p : processus) {
			if (p.getArrive_time() <= start && !p.isBlocked() && p.isArrived() && !p.isFinished()) {
				if (!processReadyToActivate.contains(p)) {
					processReadyToActivate.add(p);
				}
			}
		}

		if (processReadyToActivate.isEmpty()) {
			return null;
		}

		return processReadyToActivate.get(0);
	}

	public static Processus findProcessToActivateRobinPrio(Processus[] processus, List<Processus> processReadyToActivate, int start) {
		for (Processus p : processus) {
			if (p.getArrive_time() <= start && !p.isBlocked() && p.isArrived() && !p.isFinished()) {
				if (!processReadyToActivate.contains(p)) {
					processReadyToActivate.add(p);
				}
			}

			if(p.isBlocked() && !p.isFinished()){
				processReadyToActivate.remove(p);
			}
		}

		if (processReadyToActivate.isEmpty()) {
			return null;
		}

		processReadyToActivate.sort(Comparator
                .comparing(Processus::getPriority_level));

		return processReadyToActivate.get(0);
	}

	private static Processus findProcessToActivateSJF(Processus[] processus, int start) {
		Processus[] processReadyToActivate = new Processus[processus.length];

		int count = 0;
		for(int i = 0; i < processus.length; i++){
			if(processus[i].getArrive_time() <= start & !processus[i].isBlocked() & processus[i].isArrived() & !processus[i].isFinished()){
				processReadyToActivate[count++] = processus[i];
			}
		}

		if (count == 0) {
			return null; // Return null if no process is arrived
		}

		processReadyToActivate = Arrays.copyOf(processReadyToActivate, count);

		// Tri par arrive time et après par nom
		Arrays.sort(processReadyToActivate, Comparator
				.comparing(Processus::getExecution_time));

		return processReadyToActivate[0];
	}
	public static void moveFirstProcessToEnd(List<Processus> processReadyToActivate) {
		if (!processReadyToActivate.isEmpty()) {
			Processus firstProcess = processReadyToActivate.get(0);
			processReadyToActivate.remove(0);
			processReadyToActivate.add(firstProcess);
		}
	}

	public static void printState(ArrayList processus_list, int start) {
		if (start_at_zero) {
			System.out.println(" 0\t\tF1\t\tF2\t\tF3\t\tF4 ");
			start_at_zero = false;
		}

		System.out.printf("%-3d\t\t", start);


		int columnWidth = 10;
		for (Object obj : processus_list) {
			if (obj instanceof Processus) {
				Processus processus = (Processus) obj;
				String status = processus.getStatus(start);
				System.out.printf("%-" + columnWidth + "s", status);
			}
		}
		System.out.println();
	}


	static boolean allProcessesFinished(Processus[] processes) {
		for (Processus process : processes) {
			if (!process.finished) {
				return false;
			}
		}
		return true;
	}

}