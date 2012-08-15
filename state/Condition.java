package scripts.state;

public abstract class Condition implements
		org.powerbot.concurrent.strategy.Condition {
	public static Condition TRUE = new Condition() {
		public boolean validate() {
			return true;
		}
	};
	public static Condition FALSE = new Condition() {
		public boolean validate() {
			return false;
		}
	};

	public Condition negate() {
		return new Condition() {
			public boolean validate() {
				return !Condition.this.validate();
			}
		};
	}
	
	public Condition and(final org.powerbot.concurrent.strategy.Condition c) {
		return new Condition() {
			public boolean validate() {
				return Condition.this.validate() && c.validate();
			}
		};
	}
	
	public Condition or(final org.powerbot.concurrent.strategy.Condition c) {
		return new Condition() {
			public boolean validate() {
				return Condition.this.validate() || c.validate();
			}
		};
	}
	
	public Condition implies(final org.powerbot.concurrent.strategy.Condition c) {
		return new Condition() {
			public boolean validate() {
				return !Condition.this.validate() || c.validate();
			}
		};
	}
	
	public Condition equals(final org.powerbot.concurrent.strategy.Condition c) {
		return new Condition() {
			public boolean validate() {
				return Condition.this.validate() == c.validate();
			}
		};
	}
	
	public Condition xor(final org.powerbot.concurrent.strategy.Condition c) {
		return new Condition() {
			public boolean validate() {
				return Condition.this.validate() != c.validate();
			}
		};
	}
}
