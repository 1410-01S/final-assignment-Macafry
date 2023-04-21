import java.util.Random;
import java.util.UUID;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        Environment.setSingleton(50, 5, 5, 500, 30, 100);
        Environment env = Environment.getSingleton();
        env.status();
        int ticks = 100;

        for (int i = 0; i < ticks; i++) {
            System.out.println();
            env.tick();
            env.status();
        }

    }
}

/* Coordinate Classes */
class Point2D {
    public float x;
    public float y;

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Point2D other) {
        float dx = other.x - this.x;
        float dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Point2D interpolate(Point2D p1, Point2D p2, int weight1, int weight2) {
        float totalWeight = weight1 + weight2;
        float t = weight1 / totalWeight;

        float newX = p1.x * t + p2.x * (1 - t);
        float newY = p1.y * t + p2.y * (1 - t);

        return new Point2D(newX, newY);
    }

    public static Point2D midpoint(Point2D p1, Point2D p2) {
        return interpolate(p1, p2, 1, 1);
    }

    public Point2D displace(Vector2D displacement) {
        float newX = this.x + displacement.dx;
        float newY = this.y + displacement.dy;
        return new Point2D(newX, newY);
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + "]";
    }
}

// having a vector class simplifies the movement logic a lot.
class Vector2D {
    public float dx;
    public float dy;

    public Vector2D(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Vector2D(Point2D start, Point2D finish) {
        float dx = finish.x - start.x;
        float dy = finish.y - start.y;

        this.dx = dx;
        this.dy = dy;
    }

    public double magnitude() {
        return Math.sqrt(this.dx * this.dx + this.dy * this.dy);
    }

    public Vector2D scale(float scaleFactor) {
        return new Vector2D(this.dx * scaleFactor, this.dy * scaleFactor);
    }

    public Vector2D unitVector() {
        return this.scale((float) (1 / this.magnitude()));
    }

    public static Vector2D unitVector(float angle) {
        return new Vector2D((float) Math.cos(angle), (float) Math.sin(angle));
    }

    public Vector2D oppositeDirection() {
        return this.scale(-1);
    }

    public Vector2D add(Vector2D vec) {
        float newDx = this.dx + vec.dx;
        float newDy = this.dy + vec.dy;
        return new Vector2D(newDx, newDy);
    }
}

/* Clamp Libraries */
// makes sure a value is inside a range
// not using generics on purpose
class Clamp {
    public static int clamp(int value, int minVal, int maxVal) {
        if (value < minVal)
            return minVal;
        if (value < maxVal)
            return maxVal;
        return value;
    }

    public static float clamp(float value, float minVal, float maxVal) {
        if (value < minVal)
            return minVal;
        if (value < maxVal)
            return maxVal;
        return value;
    }

    public static double clamp(double value, double minVal, double maxVal) {
        if (value < minVal)
            return minVal;
        if (value < maxVal)
            return maxVal;
        return value;
    }

    public static int clampLow(int value, int minVal) {
        if (value < minVal)
            return minVal;
        return value;
    }

    public static float clampLow(float value, float minVal) {
        if (value < minVal)
            return minVal;
        return value;
    }

    public static double clampLow(double value, double minVal) {
        if (value < minVal)
            return minVal;
        return value;
    }

    public static int clampHigh(int value, int maxVal) {
        if (value < maxVal)
            return maxVal;
        return value;
    }

    public static float clampHigh(float value, float maxVal) {
        if (value < maxVal)
            return maxVal;
        return value;
    }

    public static double clampHigh(double value, double maxVal) {
        if (value < maxVal)
            return maxVal;
        return value;
    }
}

class ClampPoint {
    public static Point2D clamp(Point2D p, float minX, float minY, float maxX, float maxY) {
        float newX = Clamp.clamp(p.x, minX, maxX);
        float newY = Clamp.clamp(p.y, minY, maxY);

        return new Point2D(newX, newY);
    }
}

/* Entity Classes */

// this class exists for the sole reason of the compiler not crying about an
// arraylist of unparameterized LivingBeings
// aka ArrayList<LivingBeing<?>> when it should be
// ArrayList<LivingBeing<[something]>>
abstract class Entity {
    public Point2D location;
    protected String id;

    public String getID() {
        return this.id;
    }

    public boolean equals(Entity e) {
        return this.id.equals(e.getID());
    }

    public abstract void update();

    public abstract boolean shouldRemove();
}

abstract class LivingBeing<TLivingBeing extends LivingBeing<?>> extends Entity {
    protected int age;
    protected int health;
    protected Random personality;

    protected LivingBeing(Point2D location) {
        this.age = 0;
        this.health = 100;
        this.location = location;
        // using the Universally Unique Identifier class to create id's
        this.id = UUID.randomUUID().toString();
        this.personality = new Random();
    }

    protected LivingBeing(Random personality, Point2D location) {
        this.age = 0;
        this.health = 100;
        this.location = location;
        // using the Universally Unique Identifier class to create id's
        this.id = UUID.randomUUID().toString();
        this.personality = personality;
    }

    // ideally static, but couldn't make it work
    public abstract TLivingBeing randomInstance(Random personality, Environment env);

    public int getAge() {
        return this.age;
    }

    public int getHealth() {
        return this.health;
    }

    public boolean shouldDie() {
        return health < 0;
    };

    @Override
    public boolean shouldRemove() {
        return this.shouldDie();
    };

    @Override
    public void update() {
        this.age++;
        if (age > 20) {
            int ageOver20 = age - 20;
            health -= ageOver20;
        }

        Environment env = Environment.getSingleton();
        this.location = ClampPoint.clamp(location, env.minX, env.minY, env.maxX, env.maxY);
    };
}

class Berry {
    private int nutrition;
    private boolean isPoisonous;

    public int getNutrition() {
        return nutrition;
    }

    public boolean isPoisonous() {
        return isPoisonous;
    }

    public Berry(int nutrition, boolean isPoisonous) {
        this.nutrition = nutrition;
        this.isPoisonous = isPoisonous;
    }
}

class Bush extends LivingBeing<Bush> {
    private float poisonRate;
    private int berryGrowthRate;
    private ArrayList<Berry> berries;

    public Bush(float poisonRate, int berryGrowthRate, Point2D location) {
        super(location);
        this.poisonRate = poisonRate;
        this.berryGrowthRate = berryGrowthRate;
        this.berries = new ArrayList<Berry>();

    }

    public Bush(Random personality, Point2D location, float poisonRate, int berryGrowthRate) {
        super(personality, location);
        this.poisonRate = poisonRate;
        this.berryGrowthRate = berryGrowthRate;
        this.berries = new ArrayList<Berry>();

    }

    public int berryCount() {
        return berries.size();
    }

    private void growBerry() {
        // determining values for new berry
        boolean isPoisonous = this.personality.nextFloat() > poisonRate;
        int nutrition = this.personality.nextInt(3) + 1;

        // adding berry to bush
        this.berries.add(new Berry(nutrition, isPoisonous));
    }

    public Berry[] looseBerries(int amount) {

        if (this.berries.size() == 0)
            return null;

        ArrayList<Berry> lostBerries = new ArrayList<Berry>();

        for (int i = 0; i < amount && this.berries.size() > 0; i++) {
            // transfer the lost berry from the bush into the pile of lost berries
            lostBerries.add(this.berries.get(0));
            this.berries.remove(0);
        }

        Berry[] ret = new Berry[lostBerries.size()];
        lostBerries.toArray(ret);

        return ret;
    }

    @Override
    public void update() {
        super.update();
        int berriesGrown = this.berryGrowthRate + this.personality.nextInt(3) - 1;
        for (int i = 0; i < berriesGrown; i++) {
            this.growBerry();
        }
    }

    public Bush randomInstance(Random personality, Environment env) {
        Random _personality = new Random(personality.nextInt());
        int _growthRate = personality.nextInt(5) + 3;
        float _poisonRate = 0.4f * personality.nextFloat() + 0.3f;

        float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
        float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
        Point2D _loc = new Point2D(_x_loc, _y_loc);

        return new Bush(_personality, _loc, _poisonRate, _growthRate);
    }
}

// using generics to eliminate the chance of a fox mating with a rabbit and
// getting a sheep as an offspring
abstract class Animal<TAnimal extends Animal<?>> extends LivingBeing<TAnimal> {
    protected int hunger, reproduceCoolDown;
    protected Point2D home;
    protected boolean inReproduceAge;
    public final char gender;

    protected Animal(Point2D home) {
        super(home);
        this.home = home;
        this.hunger = 80;
        this.reproduceCoolDown = 0;
        this.gender = this.personality.nextInt(2) == 1 ? 'M' : 'F';
    }

    protected Animal(Random personality, Point2D home) {
        super(personality, home);
        this.home = home;
        this.hunger = 80;
        this.reproduceCoolDown = 0;
        this.gender = this.personality.nextInt(2) == 1 ? 'M' : 'F';
    }

    public void setRepCoolDown(int time) {
        this.reproduceCoolDown = time;
    }

    public void decrementHunger(int hunger) {
        this.hunger -= hunger;
    }

    public boolean canReproduce() {
        return this.inReproduceAge && this.reproduceCoolDown == 0;
    }

    public boolean isCompatible(TAnimal partner) {

        return this.canReproduce() && partner.canReproduce() && this.gender != partner.gender;
    }

    public abstract TAnimal[] tryReproduce(TAnimal partner);

    protected abstract Point2D getWanderLocation();

    // Fake 'override' so that the compiler doesn't cry
    // ideally static
    public abstract TAnimal randomInstance(Random personality, Environment env);

    protected void move(Point2D targerLocation) {
        this.location = targerLocation;
    }

    public void wander() {
        this.move(this.getWanderLocation());
    }

    // partial logic for all animals
    @Override
    public void update() {
        super.update();
        this.hunger -= personality.nextInt(6) + 1;

        if (this.reproduceCoolDown > 0)
            this.reproduceCoolDown--;

        if (this.hunger < 50) {
            this.health -= 20;
        }

        this.wander();
    }

}

class Bunny extends Animal<Bunny> {
    public final static float fleePerFear = 0.07f;
    public final static int minReproduceAge = 3;
    public final static int maxReproduceAge = 20;

    private int fertility, fear;

    public Bunny(Point2D home, int fertility, int fear) {
        super(home);
        this.fertility = fertility;
        this.fear = fear;
    }

    public Bunny(Random personality, Point2D home, int fertility, int fear) {
        super(personality, home);
        this.fertility = fertility;
        this.fear = fear;
    }

    public int getFertility() {
        return this.fertility;
    }

    public int getFear() {
        return this.fear;
    }

    @Override
    public Bunny[] tryReproduce(Bunny partner) {
        // if they can't reproduce, then no offsprings are produced
        if (!this.isCompatible(partner))
            return null;

        int children = (this.fertility + partner.getFertility());
        Bunny[] bunnies = new Bunny[children];

        // determining offspring parameters;
        for (int i = 0; i < bunnies.length; i++) {

            int childSeed = this.personality.nextInt();
            Random childPersonality = new Random(childSeed);

            int rawChildFert = (this.fertility + partner.getFertility()) / 2 +
                    childPersonality.nextInt(4) - 1;

            int rawChildFear = (this.fertility + partner.getFertility()) / 2 +
                    childPersonality.nextInt(7) - 3;

            int childFert = Clamp.clampLow(rawChildFert, 0);
            int childFear = Clamp.clamp(rawChildFear, 0, 100);

            Point2D childHome = Point2D.midpoint(this.location, partner.location);

            bunnies[i] = new Bunny(childPersonality, childHome, childFert, childFear);

        }

        // set reproduction cool down for both parents
        this.setRepCoolDown(2 + children / 3);
        partner.setRepCoolDown(2 + children / 3);

        this.decrementHunger(children);
        partner.decrementHunger(children);

        return bunnies;
    }

    @Override
    public Point2D getWanderLocation() {
        // bunnies are atracted to their homes
        Vector2D towardsHome = new Vector2D(this.location, this.home);

        // move in a random direction
        float randomAngle = 2 * (float) Math.PI * personality.nextFloat();
        Vector2D randomDir = Vector2D.unitVector(randomAngle);

        // get the final move direction
        Vector2D direction = towardsHome.unitVector()
                .scale(1.5f)
                .add(randomDir)
                .unitVector();

        // random value between -0.5 and 1.3
        double rnd = 1.8 * this.personality.nextDouble() - 0.5;

        double unitsMoved = towardsHome.magnitude() * (1 + rnd);

        // get the final destination
        return this.location.displace(direction.scale((float) unitsMoved));
    }

    public void flee(Point2D dangerLocation) {
        Vector2D badDirection = new Vector2D(this.location, dangerLocation);

        double unitsMoved = fleePerFear * fear / badDirection.magnitude();
        Vector2D getAway = badDirection.oppositeDirection()
                .unitVector()
                .scale((float) unitsMoved);

        Point2D safePoint_probably = this.location.displace(getAway);

        this.move(safePoint_probably);
    }

    public Berry[] gather(Bush foodSource) {
        int idealBerries = (100 - this.hunger) / 2 + 10;

        return foodSource.looseBerries(idealBerries);
    }

    public void eat(Berry b) {
        if (b.isPoisonous()) {
            this.health -= 3;
        } else {
            this.hunger += b.getNutrition();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.age == minReproduceAge)
            this.inReproduceAge = true;
        if (this.age == maxReproduceAge + 1)
            this.inReproduceAge = false;
    }

    @Override
    public Bunny randomInstance(Random personality, Environment env) {
        Random _personality = new Random(personality.nextInt());
        int _fert = personality.nextInt(5) + 3;
        int _fear = personality.nextInt(81) + 10;

        float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
        float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
        Point2D _loc = new Point2D(_x_loc, _y_loc);

        return new Bunny(_personality, _loc, _fert, _fear);
    }

}

class Fox extends Animal<Fox> {
    public final static float huntPerAgg = 0.05f;
    public final static float wanderPerAgg = 0.35f;
    public final static int minReproduceAge = 4;
    public final static int maxReproduceAge = 30;

    private int aggresiveness;

    public Fox(Point2D home, int aggresiveness) {
        super(home);
        this.aggresiveness = aggresiveness;
    }

    public Fox(Random personality, Point2D home, int aggresiveness) {
        super(personality, home);
        this.aggresiveness = aggresiveness;
    }

    public int getAggresiveness() {
        return this.aggresiveness;
    }

    @Override
    public Fox[] tryReproduce(Fox partner) {

        if (!this.isCompatible(partner))
            return null;
        // TODO Auto-generated method stub

        int children = this.personality.nextInt(3) + 2;
        Fox[] pups = new Fox[children];

        // determining offspring parameters;
        for (int i = 0; i < pups.length; i++) {

            int childSeed = this.personality.nextInt();
            Random childPersonality = new Random(childSeed);

            int rawAggresive = (this.aggresiveness + partner.getAggresiveness()) / 2 +
                    childPersonality.nextInt(7) - 3;

            int childAggr = Clamp.clamp(rawAggresive, 0, 100);

            Point2D childHome = Point2D.midpoint(this.location, partner.location);

            pups[i] = new Fox(childPersonality, childHome, childAggr);

        }

        // set reproduction cool down for both parents
        this.setRepCoolDown(2 * children);
        partner.setRepCoolDown(2 * children);

        this.decrementHunger(10 * children);
        partner.decrementHunger(10 * children);

        return pups;
    }

    @Override
    protected Point2D getWanderLocation() {
        // Foxes simply move in a random direction
        float randomAngle = 2 * (float) Math.PI * personality.nextFloat();
        // squaring it yields a uniform distribution over a circle
        // cubing it makes it more likely to be near the edges of a circle
        float rand4magnitude = (float) Math.pow(personality.nextFloat(), 3);
        float magnitude = this.aggresiveness * wanderPerAgg * rand4magnitude;

        Vector2D displacement = Vector2D.unitVector(randomAngle).scale(magnitude);
        return this.location.displace(displacement);
    }

    public Bunny hunt(Bunny prey) {
        prey.flee(this.location);
        double distanceToPrey = this.location.distance(prey.location);
        float coverableDistance = this.aggresiveness * huntPerAgg;

        this.health -= (int) (distanceToPrey / 2);

        if (distanceToPrey < coverableDistance) {
            this.move(prey.location);
            return prey;
        }

        return null;
    }

    public void eat(Bunny prey) {
        this.hunger += prey.getHealth() / 2;
    }

    @Override
    public void update() {
        super.update();
        if (this.age == minReproduceAge)
            this.inReproduceAge = true;
        if (this.age == maxReproduceAge + 1)
            this.inReproduceAge = false;

        if (this.age > maxReproduceAge && this.aggresiveness > 20) {
            this.aggresiveness--;
        }
    }

    @Override
    public Fox randomInstance(Random personality, Environment env) {
        Random _personality = new Random(personality.nextInt());
        int _aggr = personality.nextInt(81) + 10;

        float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
        float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
        Point2D _loc = new Point2D(_x_loc, _y_loc);

        return new Fox(_personality, _loc, _aggr);
    }

}

/* Environment & helper classes */
class EntityListFunctions {
    public static ArrayList<Entity> getNearbyEntities(Entity target, ArrayList<Entity> entities, double maxDistance) {
        ArrayList<Entity> nearbyEntities = new ArrayList<Entity>();

        for (Entity e : entities) {
            if (target.location.distance(e.location) <= maxDistance && !target.equals(e)) {
                nearbyEntities.add(e);
            }
        }

        return nearbyEntities;
    }

    // Generics did not want to cooperate for this
    public static ArrayList<Bunny> getBunnies(ArrayList<Entity> entities) {
        ArrayList<Bunny> bunnies = new ArrayList<Bunny>();

        for (Entity e : entities) {
            if (e instanceof Bunny) {
                bunnies.add((Bunny) e);
            }
        }

        return bunnies;
    }

    public static ArrayList<Fox> getFoxes(ArrayList<Entity> entities) {
        ArrayList<Fox> foxes = new ArrayList<Fox>();

        for (Entity e : entities) {
            if (e instanceof Fox) {
                foxes.add((Fox) e);
            }
        }

        return foxes;
    }

    public static ArrayList<Bush> getBushes(ArrayList<Entity> entities) {
        ArrayList<Bush> bushes = new ArrayList<Bush>();

        for (Entity e : entities) {
            if (e instanceof Bush) {
                bushes.add((Bush) e);
            }
        }

        return bushes;
    }

    public static int getIndex(Entity target, ArrayList<Entity> entities) {

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (target.equals(e)) {
                return i;
            }
        }

        return -1;
    }
}

class StaticRootInstances {

    public static Bunny rootBunny() {
        return new Bunny(new Random(0), null, 0, 0);
    }

    public static Fox rootFox() {
        return new Fox(new Random(0), null, 0);
    }

    public static Bush rootBush() {
        return new Bush(new Random(0), null, 0, 0);
    }
}

// using the singleton pattern because I feel like LivingBeing instances
// shouldn't have access to the environment
// but the class living being needs a reference to this class to make sure
// entities don't go out of bounds.
class Environment {
    public final float minX, minY, maxX, maxY;
    private float bushSpawnRate;
    private ArrayList<Entity> entities;
    private Random chaos;

    public static Environment singleton;

    private Environment(float minX, float minY, float maxX, float maxY,
            float bushSpawnRate, int randSeed,
            int initialBunnies, int initialFoxes, int initialBushes) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.bushSpawnRate = bushSpawnRate;

        this.entities = new ArrayList<Entity>();
        this.chaos = new Random(randSeed);

        Bunny rootBunny = StaticRootInstances.rootBunny();
        for (int i = 0; i < initialBunnies; i++) {
            this.entities.add(rootBunny.randomInstance(this.chaos, this));
        }

        Fox rootFox = StaticRootInstances.rootFox();
        for (int i = 0; i < initialFoxes; i++) {
            this.entities.add(rootFox.randomInstance(this.chaos, this));
        }

        Bush rootBush = StaticRootInstances.rootBush();
        for (int i = 0; i < initialBushes; i++) {
            this.entities.add(rootBush.randomInstance(this.chaos, this));
        }

    }

    public static Environment getSingleton() {
        if (singleton == null)
            throw new NullPointerException("Singleton hasn't been initialized");

        return singleton;
    }

    public static void setSingleton(float squareRadius, float bushSpawnRate, int randSeed,
            int initialBunnies, int initialFoxes, int initialBushes) {

        singleton = new Environment(-squareRadius, -squareRadius, squareRadius, squareRadius,
                bushSpawnRate, randSeed, initialBunnies, initialFoxes, initialBushes);
        singleton.updateAll();
    }

    /* Tick components */
    private void spawnBushes() {
        int bushesSpawned = (int) (this.bushSpawnRate * (1 + 0.4f * this.chaos.nextFloat() - 0.2f));

        Bush rootBush = StaticRootInstances.rootBush();
        for (int i = 0; i < bushesSpawned; i++) {
            this.entities.add(rootBush.randomInstance(this.chaos, this));
        }
    }

    private void huntGather() {
        ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);
        for (Bunny bun : bunnies) {
            this.gather(bun);
        }

        ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
        for (Fox fox : foxes) {
            this.hunt(fox);
        }

    }

    private void hunt(Fox fox) {
        for (int huntAttempts = 0; huntAttempts < 3; huntAttempts++) {
            // get nearby bunnies
            ArrayList<Bunny> possiblePreys = EntityListFunctions.getBunnies(
                    EntityListFunctions.getNearbyEntities(fox, this.entities, Fox.huntPerAgg * 100));

            if (possiblePreys.size() == 0) {
                fox.wander();
                continue;
            }
            // choose prey
            int randomPreyIndex = this.chaos.nextInt(possiblePreys.size());
            Bunny prey = possiblePreys.get(randomPreyIndex);

            // hunt
            Bunny food = fox.hunt(prey);

            // eat & kill bunny
            if (food != null) {
                fox.eat(food);
                int foodIndex = EntityListFunctions.getIndex(food, this.entities);
                this.entities.remove(foodIndex);
            }
        }
    }

    private void gather(Bunny bun) {
        for (int huntAttempts = 0; huntAttempts < 5; huntAttempts++) {
            // get nearby bunnies
            ArrayList<Bush> possibleTarget = EntityListFunctions.getBushes(
                    EntityListFunctions.getNearbyEntities(bun, this.entities, Bunny.fleePerFear * 100));

            if (possibleTarget.size() == 0) {
                bun.wander();
                continue;
            }
            // choose target
            int randomTargetIndex = this.chaos.nextInt(possibleTarget.size());
            Bush target = possibleTarget.get(randomTargetIndex);

            // gather berries
            Berry[] food = bun.gather(target);

            // eat berries
            if (food != null) {
                for (Berry b : food) {
                    bun.eat(b);
                }
            }
        }
    }

    private void mate() {
        ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);

        for (Bunny bun : bunnies) {
            ArrayList<Bunny> nearbyBunnies = EntityListFunctions.getBunnies(
                    EntityListFunctions.getNearbyEntities(bun, this.entities, Bunny.fleePerFear * 100));

            for (Bunny possibleMate : nearbyBunnies) {
                Bunny[] offspring = bun.tryReproduce(possibleMate);

                if (offspring == null)
                    continue;

                // else
                for (Bunny child : offspring) {
                    this.entities.add(child);
                }

                break;
                // end else
            }
        }

        ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
        for (Fox fox : foxes) {
            ArrayList<Fox> nearbyFoxes = EntityListFunctions.getFoxes(
                    EntityListFunctions.getNearbyEntities(fox, this.entities, Fox.huntPerAgg * 100));

            for (Fox possibleMate : nearbyFoxes) {
                Fox[] offspring = fox.tryReproduce(possibleMate);

                if (offspring == null)
                    continue;

                // else
                for (Fox child : offspring) {
                    this.entities.add(child);
                }

                break;
                // end else
            }
        }
    }

    private void updateAll() {
        for (Entity e : this.entities) {
            e.update();
        }
    }

    private void removeDead() {
        // iterating in reverse to avoid deletion induced skips
        for (int i = this.entities.size() - 1; i >= 0; i--) {
            Entity e = this.entities.get(i);
            if (e.shouldRemove()) {
                this.entities.remove(i);
            }
        }
    }

    /* Moving the simulation forward */
    public void tick() {
        this.spawnBushes();
        this.mate();
        this.huntGather();
        this.updateAll();
        this.removeDead();
    }

    public void tick(int ticks) {
        for (int i = 0; i < ticks; i++) {
            this.tick();
        }
    }

    public void status() {
        ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);
        ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
        ArrayList<Bush> bushes = EntityListFunctions.getBushes(this.entities);

        System.out.println("Bushes: " + bushes.size());
        System.out.println("Bunnies: " + bunnies.size());
        System.out.println("Foxes: " + foxes.size());
    }
}