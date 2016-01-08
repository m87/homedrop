import org.homedrop.Command;
import org.homedrop.Plugin;
import org.homedrop.Result;

import java.util.List;

public class PrintPlugin implements Plugin{
    @Override
    public Result handleCommand(String cmd, String[] args) {
        System.out.println(cmd+" ");
        return null;
    }

    @Override
    public List<Command> introduceCommands() {
        return null;
    }
}
