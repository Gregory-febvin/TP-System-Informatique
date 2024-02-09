import java.awt.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

class Dispatcher{

	private static IOCommandes ecran;
	private static ArrayList<Processus> processusList;
	private static JTextArea resultTextArea;

	static int second = 0, start = 0, time = 0;
	static boolean start_at_zero = true;

	private static ArrayList<ArrayList<String>> dataTableau;
	public static void main(String[] args) {
		ecran = new IOCommandes();

		// Fenetre
		JFrame frame = new JFrame("Processus Ordonnancement");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setLayout(new BorderLayout());

		// Liste fichiers
		JPanel filePanel = new JPanel();
		JLabel fileLabel = new JLabel("Choisir un fichier à ordonnancer: ");
		String[] fileOptions = {"processus_1.txt", "processus_2.txt", "processus_TD.txt"};
		JComboBox<String> fileComboBox = new JComboBox<>(fileOptions);
		filePanel.add(fileLabel);
		filePanel.add(fileComboBox);

		// Liste des politiques
		JPanel algoPanel = new JPanel();
		JLabel algoLabel = new JLabel("Choisir une politique d'ordonnancement: ");
		String[] algoOptions = {"FIFO", "FIFO Priorité", "Round Robin", "Round Robin Priorité", "FiFo Priorité avec préemption", "Round Robin préemption", "Round Robin Priorité préemption", "SJF"};
		JComboBox<String> algoComboBox = new JComboBox<>(algoOptions);
		algoPanel.add(algoLabel);
		algoPanel.add(algoComboBox);

		// Bouton start
		JButton startButton = new JButton("Commencer");
		JPanel buttonPanel = new JPanel();

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = (String) fileComboBox.getSelectedItem();
				String filePath = "files/" + fileName;
				File file = new File(filePath);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(frame, "Le fichier spécifié n'existe pas.");
					return;
				}

				String lireFile = ecran.lireFile(file);
				Processus[] processus = ecran.tableProcess(lireFile);

				processusList = new ArrayList<>();
				processusList.addAll(Arrays.asList(processus));

				String selectedAlgo = (String) algoComboBox.getSelectedItem();
				processSelectedAlgorithm(selectedAlgo, processus, frame);
			}
		});
		buttonPanel.add(startButton);

		resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(resultTextArea);

		Panel containerPanel = new Panel();
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
		containerPanel.add(filePanel);
		containerPanel.add(algoPanel);


		frame.add(containerPanel, BorderLayout.NORTH);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	private static void processSelectedAlgorithm(String selectedAlgo, Processus[] processus, Component frame) {

		dataTableau = new ArrayList<>();
		for (int i = 0; i < processusList.size() + 1; i++) {
			dataTableau.add(new ArrayList<>());
		}

		switch (selectedAlgo) {
			case "FIFO":
				System.out.println("--------FIFO policy--------");
				/* FIFO (sans I/O, non-préantif et sans priorité) */

				FIFObasic(processusList, processus);

				break;
			case "FIFO Priorité":
				System.out.println("--------FIFO Priorité--------");
				/* FIFO (sans I/O, non-préantif et avec priorité) */

				FIFOPrio(processusList, processus);

				break;
			case "Round Robin":
				System.out.println("--------Round Robin--------");
				System.out.println("WIP");

				Robin(processusList, processus);

				break;
			case "Round Robin Priorité":
				System.out.println("--------Round Robin Priorité--------");
				System.out.println("WIP");

				RobinPrio(processusList, processus);

				break;

			case "FiFo Priorité avec préemption":
				System.out.println("--------FIFO Priorité Préemption--------");

				FIFOPrioPremption(processusList, processus);

				break;

			case "Round Robin préemption":
				System.out.println("--------Rand Robin Préemption--------");

				RandRobinPreemption(processusList, processus);

				break;

			case "Round Robin Priorité préemption":
				System.out.println("--------Rand Robin Priorité Préemption--------");

				RandRobinPrioPreemption(processusList, processus);

				break;

			case "SJF":
				System.out.println("--------SJF--------");

				SJF(processusList, processus);

				break;
		}
		displayResults(selectedAlgo, frame);
	}

	private static void displayResults(String selectedAlgo, Component frame) {
		// Vide le tableau
		resultTextArea.setText("");

		resultTextArea.append("--------" + selectedAlgo + "--------\n");
		resultTextArea.append(" 0\t\tF1\t\tF2\t\tF3\t\tF4\n");

		// Loop du dataTableau pour remplir le tableaux
		for (List<String> row : dataTableau) {
			for (String status : row) {
				resultTextArea.append(status + "\t\t");
			}
			resultTextArea.append("\n");
		}

		frame.revalidate();
		frame.repaint();
	}


	private static void SJF(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;
		while (allProcessesFinished(processus)){

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

		while (allProcessesFinished(processus)){

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

		while (allProcessesFinished(processus)){

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
		while (allProcessesFinished(processus)){

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

		while (allProcessesFinished(processus)){

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

		while (allProcessesFinished(processus)){

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

	public static void FIFObasic(ArrayList processusList, Processus[] processus){
		start = 0;
		time = 0;
		while (allProcessesFinished(processus)){

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
			printState(processusList, start);
			if(processToActivate != null){
				processToActivate.setActif(false);
			}
		}
	}

	private static void FIFOPrio(ArrayList processusList, Processus[] processus) {
		start = 0;
		time = 0;
		while (allProcessesFinished(processus)){

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
        for (Processus value : processus) {
            if (value.isArrived() & !value.isFinished() & value.blocked & value.getBlockedTimeEnd() <= start) {
                value.setBlocked(false);
            }
        }
	}

	private static void setProcessToArrived(Processus[] processus) {
		for(int i = 0; i < processus.length; i++){
			if(!processus[i].isArrived() & !processus[i].isFinished() & processus[i].getArrive_time() <= start ){
				processus[i].setArrived(true);
			}
		}
	}

	public static Processus findProcessToActivate(Processus[] processus, int start){
		Processus[] processReadyToActivate = new Processus[processus.length];

		int count = 0;
        for (Processus value : processus) {
            if (value.getArrive_time() <= start & !value.isBlocked() & value.isArrived() & !value.isFinished()) {
                processReadyToActivate[count++] = value;
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
        for (Processus value : processus) {
            if (value.getArrive_time() <= start & !value.isBlocked() & value.isArrived() & !value.isFinished()) {
                processReadyToActivate[count++] = value;
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
        for (Processus value : processus) {
            if (value.getArrive_time() <= start & !value.isBlocked() & value.isArrived() & !value.isFinished()) {
                processReadyToActivate[count++] = value;
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

	public static void printState(ArrayList processusList, int start) {
		if (start_at_zero) {
			System.out.println(" 0\t\tF1\t\tF2\t\tF3\t\tF4 ");
			start_at_zero = false;
		}

		System.out.printf("%-3d\t\t", start);


		int columnWidth = 10;
		for (Object obj : processusList) {
			if (obj instanceof Processus processus) {
				String status = processus.getStatus(start);
				System.out.printf("%-" + columnWidth + "s", status);
			}
		}
		System.out.println();

		ArrayList<String> row = new ArrayList<>();
		row.add(Integer.toString(start));
		for (Object obj : processusList) {
			if (obj instanceof Processus) {
				Processus p = (Processus) obj;
				String status = p.getStatus(start);
				row.add(status);
			}
		}
		dataTableau.add(row);
	}

	static boolean allProcessesFinished(Processus[] processes) {
		for (Processus process : processes) {
			if (!process.finished) {
				return true;
			}
		}
		return false;
	}

}