package project3;

public class FlightKey implements Comparable {
	private Comparable dep;
	private Comparable tem;
	private Comparable date;
	private Comparable date2;
	private Comparable time;
	private Comparable time2;
	private Comparable hour;

	public FlightKey(Comparable dep, Comparable tem, Comparable date,
			Comparable time) {
		this.dep = dep;
		this.tem = tem;
		date2 = date;
		if (((String) date).contains("/")) {
			String[] dates = ((String) date).split("/");
			this.date = dates[2] + dates[0] + dates[1];
		} else {
			this.date = date;
		}
		time2 = time;
		try {
			String[] times = ((String) time).split(":");
			this.time = Integer.parseInt(times[0]) * 60
					+ Integer.parseInt(times[1]);
			this.hour = Integer.parseInt(times[0]);
		} catch (Exception e) {
			this.time = time;
		}
	}

	public Comparable getDep() {
		return dep;
	}

	public Comparable getTem() {
		return tem;
	}

	public Comparable getDate() {
		return date;
	}

	public Comparable getTime() {
		return time;
	}

	@Override
	public int compareTo(Object o) {
		FlightKey other = (FlightKey) o;
		if (this.dep.compareTo(other.dep) != 0) {
			return this.dep.compareTo(other.dep);
		} else {
			if (this.tem.compareTo(other.tem) != 0) {
				return this.tem.compareTo(other.tem);
			} else {
				if (this.date.compareTo(other.date) != 0) {
					return this.date.compareTo(other.date);
				} else {
					return this.time.compareTo(other.time);
				}
			}
		}
	}

	public int compare2(FlightKey other) {
		if (this.dep.compareTo(other.dep) != 0) {
			return this.dep.compareTo(other.dep);
		} else {
			if (this.tem.compareTo(other.tem) != 0) {
				return this.tem.compareTo(other.tem);
			} else {
				return this.date.compareTo(other.date);

			}
		}
	}

	public int compare3(FlightKey other) {
		if (this.dep.compareTo(other.dep) != 0) {
			return this.dep.compareTo(other.dep);
		} else {
			return this.tem.compareTo(other.tem);
		}
	}

	public int timeDifference(FlightKey other) {
		return Math.abs((Integer) this.hour - (Integer) other.hour);

	}

	public int dayDifference(FlightKey other) {
		return Math.abs(Integer.parseInt((String) this.date)
				- Integer.parseInt((String) other.date));

	}

	public String toString() {
		return "(" + this.dep + " " + this.tem + " " + this.date2 + " "
				+ this.time2 + ")";
	}

	public Object getOrigin() {
		return this.dep;
	}

	public Object getDest() {
		return tem;
	}

	public Comparable getDate2() {
		return this.date2;
	}

	public Comparable getTime2() {
		return this.time2;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getHour() {
		return (Integer) this.hour;
	}
}
