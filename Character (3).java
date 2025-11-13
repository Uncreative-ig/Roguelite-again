import java.util.*;

public class Character {
	protected String name;
	protected int level;
	protected int health;
	protected int maxHealth;
	protected int attack;
	protected int defense;
	private Random ran = new Random();
	protected boolean neverMiss = false;
	protected int neverMissTurns = 0;
	protected boolean hopeMode = false;
	protected int hopeModeTurns = 0;

	// ====== Buffs & Debuffs ======
	protected int attBuff = 0;
	protected int defBuff = 0;
	protected int attDeBuff = 0;
	protected int defDeBuff = 0;
	protected int attBuffTurns = 0;
	protected int defBuffTurns = 0;
	protected int deAttBuffTurns = 0;
	protected int deDefBuffTurns = 0;
	protected boolean extraTurn = false;
	
	// ====== Status Effects ======
	private List<String> statusEffects = new ArrayList<>();
	private List<Integer> statusDurations = new ArrayList<>();
	protected boolean stunned = false;
	protected boolean frozen = false;
	protected boolean reflector = false;
	protected boolean isInvisible = false;
	protected int burnAttackPenalty = 0;
	protected boolean burnPenaltyApplied = false; // FIX: Track if penalty already applied

	// ====== Regen & Healing ======
	protected int regenAmount = 0;
	protected int regenDuration = 0;

	// ====== Critical Chance ======
	protected double critChance = 0.1;
	protected double tempCritBoost = 0.0;
	protected int critBoostTurns = 0;
	protected double critMultiplier = 1.5;

	// ====== Constructor ======
	public Character(String name, int level, int health, int attack, int defense) {
		this.name = name;
		this.level = level;
		this.health = this.maxHealth = health;
		this.attack = attack;
		this.defense = defense;
	}

	// ====== Combat Status ======
	public boolean isAlive() {
		return health > 0;
	}

	public void resetHealth() {
		health = maxHealth;
	}

	public boolean hasExtraTurn() {
		return extraTurn;
	}

	public void setExtraTurn(boolean t) {
		extraTurn = t;
	}

	public void takeDamage(double damage, Character attacker) {
		// Miss chance (unless never miss is active)
		if (attacker != null) {
			if (attacker.neverMiss) {
				// Never miss when Sadness emotion is active
			} else if (ran.nextInt(18) == 0) {
				System.out.println(attacker.name + " missed");
				return;
			}
		}

		int totalDefense = defense + defBuff - defDeBuff;
		double bouncedDamage = 0;

		if (hasReflector()) {
			System.out.println(name + " has a reflector! 50% of damage is bounced back!");
			bouncedDamage = damage * 0.50;
			damage *= 0.50;
		}

		// Apply defense reduction
		int reduced = (int)Math.max(1, damage - totalDefense);
		
		// Check Hope Mode AFTER calculating final damage
		if (hopeMode && health - reduced < 1) {
			health = 1;
			System.out.println(name + " refuses to fall! (Hope Mode)");
		} else {
			health -= reduced;
			if (health < 0) health = 0;
			System.out.println(name + " takes " + reduced + " damage. (HP: " + health + ")");
		}

		// Bounce damage
		if (bouncedDamage > 0 && attacker != null) {
			int bouncedReduced = (int)Math.max(1, bouncedDamage - (attacker.defense + attacker.defBuff - attacker.defDeBuff));
			attacker.health -= bouncedReduced;
			if (attacker.health < 0) attacker.health = 0;
			System.out.println(attacker.name + " takes " + bouncedReduced + " bounced damage. (HP: " + attacker.health + ")");
		}
	}

	public int getRanDmg(int base, Character attacker) {
		int damage = base + attack + ran.nextInt(5) + attacker.attBuff - attacker.attDeBuff;

		// Calculate current crit chance (base + temporary boost)
		double currentCritChance = 0.1 + this.tempCritBoost;
		
		if (ran.nextDouble() < currentCritChance) {
			System.out.println(name + " lands a CRITICAL HIT!");
			damage = (int)(damage * critMultiplier);
		}

		return damage;
	}

	public void heal(int amount) {
		health = Math.min(maxHealth, health + amount);
		System.out.println(name + " heals for " + amount + " (HP: " + health + ")");
	}

	// ====== Buffs ======
	public void applyBuff(String type, int amount, int turns) {
		if (type.equals("attack")) {
			this.attBuff += amount;
			this.attBuffTurns = Math.max(this.attBuffTurns, turns); // FIX: Use max to prevent overwrite
			System.out.println(name + " gains +" + amount + " attack buff for " + turns + " turns");
		} else if (type.equals("defense")) {
			this.defBuff += amount;
			this.defBuffTurns = Math.max(this.defBuffTurns, turns); // FIX: Use max
			System.out.println(name + " gains +" + amount + " defense buff for " + turns + " turns");
		} else if (type.equals("both")) {
			this.attBuff += amount;
			this.defBuff += amount;
			this.attBuffTurns = Math.max(this.attBuffTurns, turns); // FIX: Use max
			this.defBuffTurns = Math.max(this.defBuffTurns, turns); // FIX: Use max
			System.out.println(name + " gains +" + amount + " attack and defense buff for " + turns + " turns");
		}
	}

	public void applyDeBuff(String type, int amount, int turns) {
		if (type.equals("attack")) {
			this.attDeBuff += amount;
			this.deAttBuffTurns = Math.max(this.deAttBuffTurns, turns); // FIX: Use max
			System.out.println(name + " loses -" + amount + " attack for " + turns + " turns");
		} else if (type.equals("defense")) {
			this.defDeBuff += amount;
			this.deDefBuffTurns = Math.max(this.deDefBuffTurns, turns); // FIX: Use max
			System.out.println(name + " loses -" + amount + " defense for " + turns + " turns");
		} else if (type.equals("both")) {
			this.attDeBuff += amount;
			this.defDeBuff += amount;
			this.deAttBuffTurns = Math.max(this.deAttBuffTurns, turns); // FIX: Use max
			this.deDefBuffTurns = Math.max(this.deDefBuffTurns, turns); // FIX: Use max
			System.out.println(name + " loses -" + amount + " attack and defense for " + turns + " turns");
		}
	}

	public void updateBuffs() {
		// Attack buff countdown
		if (attBuffTurns > 0) {
			attBuffTurns--;
			if (attBuffTurns == 0) {
				System.out.println(name + "'s attack buff wore off! (was +" + attBuff + ")");
				attBuff = 0;
			}
		}

		// Defense buff countdown
		if (defBuffTurns > 0) {
			defBuffTurns--;
			if (defBuffTurns == 0) {
				System.out.println(name + "'s defense buff wore off! (was +" + defBuff + ")");
				defBuff = 0;
			}
		}

		// Attack debuff countdown
		if (deAttBuffTurns > 0) {
			deAttBuffTurns--;
			if (deAttBuffTurns == 0) {
				System.out.println(name + "'s attack debuff wore off! (was -" + attDeBuff + ")");
				attDeBuff = 0;
			}
		}

		// Defense debuff countdown
		if (deDefBuffTurns > 0) {
			deDefBuffTurns--;
			if (deDefBuffTurns == 0) {
				System.out.println(name + "'s defense debuff wore off! (was -" + defDeBuff + ")");
				defDeBuff = 0;
			}
		}

		// Crit boost countdown
		if (critBoostTurns > 0) {
			critBoostTurns--;
			if (critBoostTurns == 0) {
				tempCritBoost = 0.0;
				System.out.println(name + "'s critical buff has worn off");
			}
		}

		// Never miss countdown
		if (neverMissTurns > 0) {
			neverMissTurns--;
			if (neverMissTurns == 0) {
				neverMiss = false;
				System.out.println(name + " can miss again");
			}
		}

		// Hope mode countdown
		if (hopeModeTurns > 0) {
			hopeModeTurns--;
			if (hopeModeTurns == 0) {
				hopeMode = false;
				System.out.println(name + "'s Hope Mode has ended!");
			}
		}
	}

	// ====== Status System ======
	public void setStatus(String status, int duration) {
		status = status.toLowerCase();

		int index = statusEffects.indexOf(status);
		if(index != -1) {
			// Status already exists, extend duration
			statusDurations.set(index, Math.max(statusDurations.get(index), duration)); // FIX: Use max
		} else {
			statusEffects.add(status);
			statusDurations.add(duration);
		}
		System.out.println(name + " is now " + status + " for " + duration + " turns");
	}

	public void applyStatus() {
		for(int i = 0; i < statusEffects.size(); i++) {
			String effect = statusEffects.get(i);
			int duration = statusDurations.get(i);

			if (duration <= 0) continue;

			if (effect.equals("frozen")) {
				frozen = true;
			} else if (effect.equals("stunned")) {
				stunned = true;
			} else if (effect.equals("burned")) {
				takeDamage(5, null);
				// FIX: Only apply attack penalty ONCE per burn status
				if (!burnPenaltyApplied) {
					attack -= 2;
					burnAttackPenalty = 2;
					burnPenaltyApplied = true;
				}
			} else if (effect.equals("poisoned")) {
				takeDamage(12, null);
			} else if (effect.equals("invisible")) {
				isInvisible = true;
			} else if (effect.equals("reflect")) {
				reflector = true;
			}
			
			// Decrement duration
			statusDurations.set(i, duration - 1);
		}
	}

	public void cleanupStatuses() {
		for (int i = statusEffects.size() - 1; i >= 0; i--) { // FIX: Iterate backwards to avoid index issues
			String effect = statusEffects.get(i);
			int duration = statusDurations.get(i);

			if (duration <= 0) {
				clearStatus(effect);
				statusEffects.remove(i);
				statusDurations.remove(i);
			}
		}
	}

	public void clearStatus(String effect) {
		if (effect.equals("frozen")) {
			frozen = false;
			System.out.println(name + "'s frozen status has worn off");
		} else if (effect.equals("stunned")) {
			stunned = false;
			System.out.println(name + "'s stunned status has worn off");
		} else if (effect.equals("burned")) {
			attack += burnAttackPenalty;
			burnAttackPenalty = 0;
			burnPenaltyApplied = false; // FIX: Reset flag
			System.out.println(name + "'s burned status has worn off");
		} else if (effect.equals("invisible")) {
			isInvisible = false;
			System.out.println(name + "'s invisible status has worn off");
		} else if (effect.equals("reflect")) {
			reflector = false;
			System.out.println(name + "'s reflect status has worn off");
		} else if (effect.equals("poisoned")) {
			System.out.println(name + "'s poisoned status has worn off");
		} else if (effect.equals("all")) {
			// FIX: Properly clear ALL statuses
			frozen = false;
			stunned = false;
			attack += burnAttackPenalty;
			burnAttackPenalty = 0;
			burnPenaltyApplied = false;
			isInvisible = false;
			reflector = false;
			statusEffects.clear();
			statusDurations.clear();
			System.out.println(name + "'s all status effects have been cleared");
		}
	}

	public boolean hasReflector() {
		return reflector;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public boolean isStunned() {
		return stunned;
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	// ====== Regen ======
	public void setRegen(int amount, int duration) {
		this.regenAmount = amount;
		this.regenDuration = duration;
		System.out.println(name + " will regenerate " + amount + " HP for " + duration + " turns");
	}

	public void applyRegen() {
		if (regenAmount > 0 && regenDuration > 0) {
			heal(regenAmount);
			regenDuration--;
			if (regenDuration == 0) {
				regenAmount = 0;
				System.out.println(name + "'s regeneration has ended");
			}
		}
	}

	// ====== Crit Chance ======
	public void setCritChance(double amt, int duration) {
		this.tempCritBoost = amt;
		this.critBoostTurns = duration;
		System.out.println(name + " gains +" + (int)(amt * 100) + "% crit chance for " + duration + " turns");
	}

	// ====== Display ======
	public void displayStats() {
		System.out.print(name + " (Level " + level + ") HP: " + health + "/" + maxHealth);
		
		// Display active buffs/debuffs
		List<String> activeEffects = new ArrayList<>();
		if (attBuff > 0) activeEffects.add("ATK+" + attBuff + "(" + attBuffTurns + ")");
		if (defBuff > 0) activeEffects.add("DEF+" + defBuff + "(" + defBuffTurns + ")");
		if (attDeBuff > 0) activeEffects.add("ATK-" + attDeBuff + "(" + deAttBuffTurns + ")");
		if (defDeBuff > 0) activeEffects.add("DEF-" + defDeBuff + "(" + deDefBuffTurns + ")");
		
		if (!activeEffects.isEmpty()) {
			System.out.print(" | Buffs: " + String.join(", ", activeEffects));
		}
		
		// Display statuses
		if (!statusEffects.isEmpty()) {
			System.out.print(" | Status: ");
			for (int i = 0; i < statusEffects.size(); i++) {
				System.out.print(statusEffects.get(i) + "(" + statusDurations.get(i) + ")");
				if (i < statusEffects.size() - 1) {
					System.out.print(", ");
				}
			}
		}
		System.out.println();
	}

	// ====== Emotion-Related ======
	public void setNeverMiss(boolean value, int turns) {
		this.neverMiss = value;
		this.neverMissTurns = turns;
		if (value) {
			System.out.println(name + " will never miss for " + turns + " turns!");
		}
	}

	public void setHopeMode(boolean value, int turns) {
		this.hopeMode = value;
		this.hopeModeTurns = turns;
		if (value) {
			System.out.println(name + " enters Hope Mode for " + turns + " turns - refuses to die!");
		}
	}

	public boolean isInHopeMode() {
		return hopeMode;
	}

	public boolean hasNeverMiss() {
		return neverMiss;
	}

	// ====== Getters ======
	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public int getAttack() {
		return attack;
	}
	
	public int getDefense() {
		return defense;
	}
}
