public class TramLinkSimulation extends Process {
    int numberofstops;
    int numberoftrams;
    int numberofpassedstops = 0;
    int maxqueue = 0;
    int number_of_max_stop_length = 0;
    boolean smer_of_max_length_stop = true;
    public double simPeriod = 10;
    Stop[] stopsfirstrline;
    Stop[] stopssecondline;
    Head depointo = new Head();
    Head depoout = new Head();
    Head[] forbusstups;
    Head[] forbusstupsback;
    double time;
    Random random = new Random(5);

    long starttime = System.currentTimeMillis();
    class Tram extends Process {
        int numberofcurrentstop = 0;
        boolean depofrom = true;
        @Override
        protected void actions() {
            while(time()<= simPeriod) {
//                System.out.println(time() + ' ' + simPeriod);
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
        Boolean depo;
        public Depo(Boolean smer){

            depo = smer;
            //System.out.println("Depo konstruktor " + depo.cardinal());
            //System.out.println("Depointo konstruktor" + depointo.cardinal());
        }
        @Override
        protected void actions() {
            while (time() <= simPeriod){
                //System.out.println(depo.cardinal() + "");
                System.out.println(time() + "+" + simPeriod);
                //System.out.println(!depointo.empty() + " ");
                System.out.println(depo);
                if (depo){
                    if (!depointo.empty()) {
                        System.out.println("Im there");
                        hold(0.5);
                        Tram jizda = (Tram) depointo.first();
                        jizda.out();
                        activate(jizda);
                    }
                }else {
                    //System.out.println("Im there");
                    if(!depoout.empty()){
                        System.out.println("Im there");
                        hold(0.5);
                        Tram jizda = (Tram) depoout.first();
                        jizda.out();
                        activate(jizda);
                    }
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
        stopsfirstrline = new Stop[numberofstops];
        stopssecondline = new Stop[numberofstops];
        forbusstups = new Head[numberofstops];
        forbusstupsback = new Head[numberofstops];
    }

    @Override
    protected void actions() {
        for(int i=0; i<numberoftrams;i++){
            new Tram().into(depointo);
        }
        System.out.println(depointo.cardinal());
        for(int i = 0; i<numberofstops;i++){
            stopsfirstrline[i] = new Stop(i, true);
            stopssecondline[i] = new Stop(i, false);
        }
        Depo depofirst = new Depo(true);
        Depo deposecond = new Depo(false);
        activate(depofirst);
        activate(deposecond);
        hold(simPeriod + 100);
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
