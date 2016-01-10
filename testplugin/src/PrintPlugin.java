import org.homedrop.Command;
import org.homedrop.Plugin;
import org.homedrop.Request;
import org.homedrop.Result;

import java.util.List;

public class PrintPlugin implements Plugin{
    @Override
    public Result handleRequest(Request request) {
        System.out.println(request.getCommand().getName()+" ");
        return null;
    }

    @Override
    public List<Command> introduceCommands() {
        return null;
    }
}
