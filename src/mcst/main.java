package mcst;


import rddl.sim.Simulator;

class main {
  public static void main(String[] args) throws Exception {
    Simulator.main(new String[]{"files/final_comp/rddl", "rddl.solver.mdp.uct.UCT", "elevators_inst_mdp__9", "rddl.viz.ElevatorDisplay"});
  }
}
