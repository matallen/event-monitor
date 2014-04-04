package org.jboss.eventmonitor;

import java.util.ArrayList;
import java.util.List;
import org.jboss.eventmonitor.domain.Event;
import org.jboss.eventmonitor.domain.EventProcess;

public class WorkingMemoryState {
  private Structure inStream=new Structure();
  private Structure inMemory=new Structure();
  public Structure getInStream() { return inStream; }
  public Structure getInMemory() { return inMemory; }

  public class Structure{
    private List<EventProcess> processes=new ArrayList<EventProcess>();
    private List<Event> events=new ArrayList<Event>();
    public List<EventProcess> getProcesses() { return processes; }
    public List<Event> getEvents()           { return events;    }
  }
}
