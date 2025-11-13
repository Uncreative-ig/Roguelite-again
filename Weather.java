public class Weather {
    private String name;
    private String description;
    private boolean isActive;
    
    public Weather(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = false;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    // Apply weather effects at start of turn
    public void applyStartOfTurn(Player player, Enemy enemy) {
        if (!isActive) return;
        
        switch(name) {
            // ===== ABANDONED VILLAGE WEATHER =====
            case "Fog":
                System.out.println("[Weather: Fog] - Attacks have increased miss chance!");
                break;
                
            case "Overcast":
                System.out.println("[Weather: Overcast] - Everyone feels drained...");
                break;
                
            case "Light Rain":
                System.out.println("[Weather: Light Rain] - The ground is slippery...");
                break;
                
            // ===== SCORCHED WASTELAND WEATHER =====
            case "Heatwave":
                System.out.println("[Weather: Heatwave] - The heat is unbearable!");
                player.takeDamage(5, null);
                enemy.takeDamage(5, null);
                break;
                
            case "Ash Storm":
                System.out.println("[Weather: Ash Storm] - Burning ash swirls around!");
                player.takeDamage(3, null);
                enemy.takeDamage(3, null);
                break;
                
            case "Sandstorm":
                System.out.println("[Weather: Sandstorm] - Sand stings your eyes!");
                break;
                
            // ===== FROZEN HOLLOW WEATHER =====
            case "Blizzard":
                System.out.println("[Weather: Blizzard] - Freezing winds howl!");
                break;
                
            case "Freezing Wind":
                System.out.println("[Weather: Freezing Wind] - Your limbs grow numb...");
                break;
                
            case "Heavy Snow":
                System.out.println("[Weather: Heavy Snow] - Snow weighs you down...");
                break;
                
            // ===== TWILIGHT DEPTHS WEATHER =====
            case "Darkness":
                System.out.println("[Weather: Darkness] - You can barely see...");
                break;
                
            case "Echoing Winds":
                System.out.println("[Weather: Echoing Winds] - Strange echoes fill the air...");
                break;
                
            case "Tremors":
                System.out.println("[Weather: Tremors] - The ground shakes beneath you!");
                break;
        }
    }
    
    // Modify damage based on weather
    public int modifyDamage(int damage, Character attacker, Character target) {
        if (!isActive) return damage;
        
        switch(name) {
            case "Fog":
            case "Sandstorm":
            case "Darkness":
                // 20% miss chance
                if (attacker != null && !attacker.hasNeverMiss()) {
                    java.util.Random rand = new java.util.Random();
                    if (rand.nextInt(5) == 0) {
                        System.out.println("  -> " + attacker.getName() + " missed due to " + name + "!");
                        return 0;
                    }
                }
                break;
                
            case "Overcast":
            case "Heavy Snow":
                // Reduce damage by 10%
                damage = (int)(damage * 0.9);
                break;
                
            case "Light Rain":
                // 10% chance to slip and do reduced damage
                java.util.Random rand = new java.util.Random();
                if (rand.nextInt(10) == 0) {
                    System.out.println("  -> " + attacker.getName() + " slips on wet ground!");
                    damage = (int)(damage * 0.5);
                }
                break;
                
            case "Heatwave":
            case "Ash Storm":
                // Fire-related attacks deal 20% more
                damage = (int)(damage * 1.2);
                break;
                
            case "Blizzard":
            case "Freezing Wind":
                // Ice-related attacks deal 20% more, others 10% less
                damage = (int)(damage * 0.9);
                break;
                
            case "Echoing Winds":
                // Random damage variance +/- 20%
                rand = new java.util.Random();
                double variance = 0.8 + (rand.nextDouble() * 0.4);
                damage = (int)(damage * variance);
                break;
                
            case "Tremors":
                // 15% chance for earthquake damage
                rand = new java.util.Random();
                if (rand.nextInt(7) == 0) {
                    System.out.println("  -> Earthquake! Both take extra damage!");
                    attacker.takeDamage(8, null);
                    target.takeDamage(8, null);
                }
                break;
        }
        
        return damage;
    }
    
    // Apply weather effects at end of turn
    public void applyEndOfTurn(Player player, Enemy enemy) {
        if (!isActive) return;
        
        switch(name) {
            case "Freezing Wind":
                // Small chance to freeze
                java.util.Random rand = new java.util.Random();
                if (rand.nextInt(10) == 0) {
                    if (rand.nextBoolean()) {
                        player.setStatus("frozen", 1);
                    } else {
                        enemy.setStatus("frozen", 1);
                    }
                }
                break;
                
            case "Blizzard":
                // Slow cooldowns
                player.increaseAllSkillCooldowns(1);
                enemy.increaseAllSkillCooldowns(1);
                System.out.println("  -> Blizzard slows everyone's abilities!");
                break;
        }
    }
    
    // Get weather effect description for display
    public String getEffectDescription() {
        switch(name) {
            case "Fog": return "Increased miss chance";
            case "Overcast": return "Reduced damage dealt";
            case "Light Rain": return "Chance to slip";
            case "Heatwave": return "5 damage to all per turn";
            case "Ash Storm": return "3 damage to all per turn, obscured vision";
            case "Sandstorm": return "Increased miss chance";
            case "Blizzard": return "Slows all cooldowns";
            case "Freezing Wind": return "Reduced damage, freeze chance";
            case "Heavy Snow": return "Reduced damage dealt";
            case "Darkness": return "Increased miss chance";
            case "Echoing Winds": return "Random damage variance";
            case "Tremors": return "Random earthquake damage";
            default: return "No effect";
        }
    }
}