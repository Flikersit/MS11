package tram;

public class TramLinkSimulation extends Process {
    int numberofstops;
    int numberoftrams;
    int numberofpassedstops = 0;
    int maxqueue = 0;
    int number_of_max_stop_length = 0;
    boolean smer_of_max_length_stop = true;
    double simPeriod = 200;
    Stop[] stopsfirstrline = new Stop[numberofstops];
    Stop[] stopssecondline = new Stop[numberofstops];
    Head depointo = new Head();
    Head depoout = new Head();
    Head[] forbusstups = new Head[numberofstops];
    Head[] forbusstupsback = new Head[numberofstops];
    double time;
    Random random = new Random(5);

    long starttime = System.currentTimeMillis();
    class Tram extends Process {
        int numberofcurrentstop = 0;
        boolean depofrom = true;
        @Override
        protected void actions() {
            while(true) {
                if (numberofcurrentstop > numberofstops && depofrom) {
                    hold(random.normal(15.0, 3.0));
                    into(depoout);
                    depofrom = false;
                    numberofcurrentstop = 0;
                } else if (numberofcurrentstop > numberofstops && !depofrom) {
                    hold(random.normal(15.0, 3.0));
                    depofrom = true;
                    numberofcurrentstop = 0;
                    into(depointo);
                } else if (depofrom) {
                    hold(random.normal(15.0, 3.0));
                    into(forbusstups[numberofcurrentstop]);
                    activate(stopsfirstrline[numberofcurrentstop]);
                    numberofpassedstops++;
                    numberofcurrentstop++;
                } else if (!depofrom) {
                    hold(random.normal(15.0, 3.0));
                    into(forbusstupsback[numberofcurrentstop]);
                    activate(stopssecondline[numberofcurrentstop]);
                    numberofpassedstops++;
                    numberofcurrentstop++;
                }
            }

        }
    }
    class Depo extends Process{
        Head depo;
        public Depo(Head seznam){
            depo = seznam;
        }
        @Override
        protected void actions() {
            while (time() <= simPeriod){
                if (!depo.empty()){
                    hold(5.0);
                    Tram jizda = (Tram) depo.first();
                    jizda.out();
                    activate(jizda);
                }
            }

        }
    }
    class Stop extends Process{
        int stopnumer;
        boolean smer;
        public Stop(int nuberOfstop, boolean smer){
            this.smer = smer;
            stopnumer = nuberOfstop;
        }
        @Override
        protected void actions() {
            double waitingtime = random.normal(10.0, 1.0);
            if(smer == true){
                while (!forbusstups[stopnumer].empty()) {
                    if (forbusstups[stopnumer].cardinal() > maxqueue) {
                        maxqueue = forbusstups[stopnumer].cardinal();
                        number_of_max_stop_length = stopnumer;
                        smer_of_max_length_stop = smer;
                    }
                    Tram prepered = (Tram) forbusstups[stopnumer].first();
                    hold(waitingtime);
                    prepered.out();
                    activate(prepered);
                }
            }else{
                while (!forbusstupsback[stopnumer].empty()) {
                    if (forbusstupsback[stopnumer].cardinal() > maxqueue) {
                        maxqueue = forbusstupsback[stopnumer].cardinal();
                        number_of_max_stop_length = stopnumer;
                        smer_of_max_length_stop = smer;
                    }
                    Tram prepered = (Tram) forbusstupsback[stopnumer].first();
                    hold(waitingtime);
                    prepered.out();
                    activate(prepered);
                }
            }

        }
    }
    public TramLinkSimulation(int n, int k){
        numberofstops = n;
        numberoftrams = k;
    }

    @Override
    protected void actions() {
        for(int i=1; i<numberoftrams;i++){
            new Tram().into(depointo);
        }
        for(int i = 0; i<numberofstops;i++){
            stopsfirstrline[i] = new Stop(i, true);
            stopssecondline[i] = new Stop(i, false);
        }
        Depo depofirst = new Depo(depointo);
        Depo deposecond = new Depo(depoout);
        activate(depofirst);
        activate(deposecond);
        hold(simPeriod + 1000000);
        report();

    }
    void report(){
        System.out.println(numberofstops + " Number of stops");
        System.out.println(numberoftrams + " Number of trams");
        System.out.println(numberofpassedstops + " Number of passed stops");
        System.out.println("Max queue length " + maxqueue);
        System.out.println("Smer of the max queue " + smer_of_max_length_stop);
        System.out.println("Number of the stop where we have max length " + number_of_max_stop_length);
    }
    public static void main(String[] args){
        activate(new TramLinkSimulation(6, 7));

    }
}
