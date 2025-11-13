public class BattleEvent {
    private String name;
    private String description;
    private boolean isActive;
    
    public BattleEvent(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = false;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    // Apply event at battle start
    public void applyBattleStart(Player player, Enemy enemy) {
        if (!isActive) return;
        
        System.out.println("\n[Battle Event: " + name + "]");
        System.out.println(description);
        
        switch(name) {
            // ===== ABANDONED VILLAGE EVENTS =====
            case "Crumbling Walls":
                System.out.println("Effect: Defensive abilities are 50% stronger");
                break;
                
            case "Broken Fountain":
                System.out.println("Effect: First heal each battle is doubled");
                break;
                
            case "Town Square":
                System.out.println("Effect: Open space - multi-hit attacks hit one extra time");
                break;
                
            case "Abandoned Market":
                System.out.println("Effect: Random items scattered - gain small healing at turn start");
                break;
                
            // ===== SCORCHED WASTELAND EVENTS =====
            case "Lava Pools":
                System.out.println("Effect: Attacking deals 5 damage to attacker from heat");
                break;
                
            case "Burning Ground":
                System.out.println("Effect: All fire damage increased by 50%");
                break;
                
            case "Dust Devil":
                System.out.println("Effect: Random skill cooldowns shuffled each turn");
                break;
                
            case "Scorched Ruins":
                System.out.println("Effect: Both sides deal and take 20% more damage");
                break;
                
            // ===== FROZEN HOLLOW EVENTS =====
            case "Ice Patches":
                System.out.println("Effect: 30% chance to freeze target when dealing damage");
                break;
                
            case "Frozen Lake":
                System.out.println("Effect: Reflection - 25% damage reflected back");
                break;
                
            case "Icicle Ceiling":
                System.out.println("Effect: Random icicle falls for 15 damage every 3 turns");
                break;
                
            case "Snow Drift":
                System.out.println("Effect: Movement hindered - first attack each turn deals half damage");
                break;
                
            // ===== TWILIGHT DEPTHS EVENTS =====
            case "Narrow Passage":
                System.out.println("Effect: Only single-target skills can be used");
                break;
                
            case "Bottomless Pit":
                System.out.println("Effect: Being hit has 10% chance to stun from impact");
                break;
                
            case "Crystal Formation":
                System.out.println("Effect: Crystals amplify emotions - all emotions charge 50% faster");
                break;
                
            case "Ancient Altar":
                System.out.println("Effect: Mysterious power - gain random buff at battle start");
                player.applyRandomBuffs(1);
                break;
        }
    }
    
    // Modify damage based on event
    public int modifyDamage(int damage, Character attacker, Character target, int turnCount) {
        if (!isActive) return damage;
        
        switch(name) {
            case "Burning Ground":
                // Increase fire-related damage
                damage = (int)(damage * 1.5);
                break;
                
            case "Scorched Ruins":
                // Both deal more damage
                damage = (int)(damage * 1.2);
                break;
                
            case "Frozen Lake":
                // Reflect 25% damage
                if (attacker != null) {
                    int reflected = (int)(damage * 0.25);
                    attacker.takeDamage(reflected, null);
                    System.out.println("  -> Ice reflects " + reflected + " damage back!");
                }
                break;
                
            case "Bottomless Pit":
                // Chance to stun on hit
                if (target != null) {
                    java.util.Random rand = new java.util.Random();
                    if (rand.nextInt(10) == 0) {
                        target.setStatus("stunned", 1);
                        System.out.println("  -> " + target.getName() + " is stunned from the impact!");
                    }
                }
                break;
        }
        
        return damage;
    }
    
    // Modify defense based on event
    public int modifyDefense(int defense) {
        if (!isActive) return defense;
        
        switch(name) {
            case "Crumbling Walls":
                return (int)(defense * 1.5);
        }
        
        return defense;
    }
    
    // Modify healing based on event
    public int modifyHeal(int healAmount, boolean isFirstHeal) {
        if (!isActive) return healAmount;
        
        switch(name) {
            case "Broken Fountain":
                if (isFirstHeal) {
                    System.out.println("  -> Fountain's blessing doubles the healing!");
                    return healAmount * 2;
                }
                break;
        }
        
        return healAmount;
    }
    
    // Apply effects at turn start
    public void applyTurnStart(Player player, Enemy enemy, int turnCount) {
        if (!isActive) return;
        
        switch(name) {
            case "Abandoned Market":
                // Small heal from scattered items
                java.util.Random rand = new java.util.Random();
                if (rand.nextInt(3) == 0) {
                    player.heal(5);
                    System.out.println("  -> Found a healing item in the market!");
                }
                break;
                
            case "Icicle Ceiling":
                // Falling icicles every 3 turns
                if (turnCount % 3 == 0) {
                    rand = new java.util.Random();
                    System.out.println("  -> An icicle falls from the ceiling!");
                    if (rand.nextBoolean()) {
                        player.takeDamage(15, null);
                    } else {
                        enemy.takeDamage(15, null);
                    }
                }
                break;
        }
    }
    
    // Apply effects at turn end
    public void applyTurnEnd(Player player, Enemy enemy) {
        if (!isActive) return;
        
        switch(name) {
            case "Dust Devil":
                // Shuffle random cooldowns
                java.util.Random rand = new java.util.Random();
                if (rand.nextBoolean()) {
                    // Shuffle player cooldown
                    java.util.List<Skills> skills = player.getSkills();
                    if (!skills.isEmpty()) {
                        Skills skill = skills.get(rand.nextInt(skills.size()));
                        int newCD = rand.nextInt(skill.getCooldown() + 1);
                        skill.setCurrentCooldown(newCD);
                        System.out.println("  -> Dust devil shuffles " + player.getName() + "'s cooldowns!");
                    }
                } else {
                    // Shuffle enemy cooldown
                    java.util.List<Skills> skills = enemy.getSkills();
                    if (!skills.isEmpty()) {
                        Skills skill = skills.get(rand.nextInt(skills.size()));
                        int newCD = rand.nextInt(skill.getCooldown() + 1);
                        skill.setCurrentCooldown(newCD);
                        System.out.println("  -> Dust devil shuffles " + enemy.getName() + "'s cooldowns!");
                    }
                }
                break;
        }
    }
    
    // Check if multi-hit gets bonus
    public boolean hasMultiHitBonus() {
        return isActive && name.equals("Town Square");
    }
    
    // Check if narrow passage (limits to single target)
    public boolean isNarrowPassage() {
        return isActive && name.equals("Narrow Passage");
    }
    
    // Check if ice patch freeze effect
    public boolean hasIcePatchFreeze() {
        return isActive && name.equals("Ice Patches");
    }
    
    // Apply ice patch freeze on attack
    public void applyIcePatchEffect(Character target) {
        if (!hasIcePatchFreeze()) return;
        
        java.util.Random rand = new java.util.Random();
        if (rand.nextInt(10) < 3) { // 30% chance
            target.setStatus("frozen", 1);
            System.out.println("  -> " + target.getName() + " slipped on ice and froze!");
        }
    }
    
    // Get snow drift penalty (first attack half damage)
    public boolean hasSnowDriftPenalty(int attackCount) {
        return isActive && name.equals("Snow Drift") && attackCount == 0;
    }
    
    // Get lava pool damage
    public void applyLavaPoolDamage(Character attacker) {
        if (!isActive || !name.equals("Lava Pools")) return;
        
        attacker.takeDamage(5, null);
        System.out.println("  -> " + attacker.getName() + " takes damage from lava heat!");
    }
    
    // Check if crystal formation (faster emotion charging)
    public boolean hasFasterEmotionCharge() {
        return isActive && name.equals("Crystal Formation");
    }
}