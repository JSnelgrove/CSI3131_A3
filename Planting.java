import java.util.concurrent.Semaphore;

public class Planting {
	public static void main(String args[]) {
		TA ta = new TA();
		Professor prof = new Professor(ta);
		Student stdnt = new Student(ta);

		prof.start();
		ta.start();
		stdnt.start();

		try {
			prof.join();
		} catch (InterruptedException e) {
		}
		ta.interrupt();
		stdnt.interrupt();
	}
}

class Student extends Thread {
	TA ta;

	public Student(TA taThread) {
		ta = taThread;
	}

	public void run() {
		while (true) {
			try {
				ta.maxHoles.acquire();
				ta.shovel.acquire();

				System.out.println("Student: Got the shovel");
				sleep((int) (100 * Math.random()));
				ta.incrHoleDug();
				System.out.println("Student: Hole " + ta.getHoleDug() + " Dug");
				System.out.println("Student: Letting go of the shovel");

				ta.shovel.release();
				ta.emptyHoles.release();
				if (isInterrupted())
					break;
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println("Student is done");
	}
}

class TA extends Thread {
	private int holeFilledNum = 0; // number of holes filled
	private int holePlantedNum = 0; // number of holes planted
	private int holeDugNum = 0; // number of holes dug
	private final int MAX = 5; // can only get 5 holes ahead

	Semaphore emptyHoles;
	Semaphore filledHoles;
	Semaphore maxHoles;
	Semaphore shovel;

	public TA() {
		// Initialize semaphores
		emptyHoles = new Semaphore(0);
		filledHoles = new Semaphore(0);
		maxHoles = new Semaphore(MAX);
		shovel = new Semaphore(1);
	}

	public int getMAX() {
		return MAX;
	}

	public void incrHoleDug() {
		holeDugNum++;
	}

	public int getHoleDug() {
		return holeDugNum;
	}

	public void incrHolePlanted() {
		holePlantedNum++;
	}

	public int getHolePlanted() {
		return holePlantedNum;
	}

	public void run() {
		while (true) {
			try {
				filledHoles.acquire();
				shovel.acquire();

				System.out.println("TA: Got the shovel");
				sleep((int) (100 * Math.random()));
				holeFilledNum++;
				System.out.println("TA: The hole " + holeFilledNum + " has been filled");
				System.out.println("TA: Letting go of the shovel");

				shovel.release();
				maxHoles.release();
				if (isInterrupted())
					break;
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println("TA is done");
	}
}

class Professor extends Thread {
	TA ta;

	public Professor(TA taThread) {
		ta = taThread;
	}

	public void run() {
		while (ta.getHolePlanted() <= 20) {
			try {
				ta.emptyHoles.acquire();
				sleep((int) (50 * Math.random()));
				ta.incrHolePlanted();
				System.out.println("Professor: All be advised that I have completed planting hole " + ta.getHolePlanted());
				ta.filledHoles.release();
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println("Professor: We have worked enough for today");
	}
}
