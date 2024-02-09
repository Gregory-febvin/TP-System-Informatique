//Nomprocessus	DateArrive	DureeExecution	DateDebutIO	DureeIO	Priorite

class Processus{

	String nameProcessus, stateProcString; //Process name
		float arrive_time; //Process arrive in the system at
		float remain_time; //remain execution time

	public float getExecution_time() {
		return execution_time;
	}

	float execution_time;
		float total_time;
		float exit_time; //I/O cycle execution at
		float exit_time_last; //I/O last for
		float remain_time_blocked;
		float blockedTimeEnd;
		int priority_level; // Process priority level
		boolean finished, actif, arrived, blocked;
		//Vous pouvez rajouter des variables supplémentaires si besoin


	public String getNameProcessus() {
		return nameProcessus;
	}

	public float getArrive_time() {
		return arrive_time;
	}

	public float getRemain_time() {
		return remain_time;
	}

	public float getTotal_time() {
		return total_time;
	}

	public float getExit_time() {
		return exit_time;
	}

	public float getExit_time_last() {
		return exit_time_last;
	}

	public float getBlockedTimeEnd() {
		return blockedTimeEnd;
	}

	public int getPriority_level() {
		return priority_level;
	}

	public void setBlockedTimeEnd(float actualTime) {
		this.blockedTimeEnd = actualTime + exit_time_last;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isActif() {
		return actif;
	}

	public boolean isArrived() {
		return arrived;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public float getBlockedTime() {
		return blockedTimeEnd;
	}

	public String getStatus(int start) {
		if (isFinished()) {
			return "x";
		} else if (isBlocked()) {
			return "B(" + (start - (getBlockedTimeEnd() - getExit_time_last())) + ")";
		} else if (isArrived()) {
			if (isActif()) {
				return "A(" + getTotal_time() + ")";
			} else {
				return "a(" + getTotal_time() + ")";
			}
		} else {
			return "-";
		}
	}

	Processus(String name, float ar, float rt, float iot, float iolast, int prio){
			nameProcessus=name;
			arrive_time=ar;
			remain_time=rt;
			execution_time=rt;
			total_time = 0;
			exit_time=iot;
			exit_time_last=iolast;
			priority_level=prio;
			finished = false;
			arrived = false;
			actif = false;
			blocked = false;
			remain_time_blocked = -1;
			blockedTimeEnd = -1;
			stateProcString = " ";
		}

	public float getRemain_time_blocked() {
		return remain_time_blocked;
	}

	public void setFinished(boolean finished) {
			this.finished = finished;
		}

		public void setRemain_t(float remain_t) {
			this.remain_time = remain_t;
		}

		public void setActif(boolean state) {
			this.actif = state;
		}

		public void setArrived(boolean arrived) {
			this.arrived = arrived;
		}

		public void setBlocked(boolean blocked) {
			this.blocked = blocked;
		}
}