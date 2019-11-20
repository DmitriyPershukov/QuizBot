package com.company;

import java.util.*;

public class Dialogue
{
    private static Map<String, Command> commandsList = new HashMap<>();
    private Quiz quiz = new Quiz();
    String getCommandName(String[] command)
    {
        return command[0];
    }

    String[] parseInput(String input)
    {
        return input.substring(1).split(" ");
    }

    String[] getCommandInput(String[] command)
    {
        String[] commandInput = Arrays.copyOfRange(command, 1, command.length);
        if (commandInput.length == 0)
        {
            return null;
        }
        else
        {
            return commandInput;
        }
    }
    
    Output returnQuizAnswer(String input)
    {
        Output output = new Output();
        if(input.length() >= 1 && input.substring(0,1).equals("/"))
        {
            String[] newInput = parseInput(input);
            String commandName = getCommandName(newInput);
            String[] commandInput = getCommandInput(newInput);
            if(commandsList.containsKey(commandName))
            {
                try{
                    output.text = commandsList.get(commandName).startProcess(commandInput);
                }
                catch (Exception y){
                    output.text = y.getMessage();
                }
            }
            else
            {
                output.text = "Такой команды нет";
            }
        }
        else
        {
            if(quiz.quizWantsAnswer)
            {
                output.text = checkAnswer(input);
            }
        }
        output.setAnswers(quiz);
        return output;
    }
    public Dialogue()
    {
        commandsList.put("help", new CommandWithoutInput("выводит список команд") {
            @Override
            public String process() {
                return help();
            }
        });
        commandsList.put("questionPlease", new CommandWithoutInput("задает новый вопрос") {
            @Override
            public String process() {
                return getNewQuestion();
            }
        });
        commandsList.put("restart", new CommandWithoutInput("начинает новую игру") {
            @Override
            public String process() {
                return restart();
            }
        });
        commandsList.put("shuffleQuestions", new CommandWithoutInput("перемешивает вопросы") {
            @Override
            public String process() {
                return shuffleQuestions();
            }
        });
        commandsList.put("closeProgramm", new CommandWithoutInput("закрывает программу") {
            @Override
            public String process() {
                return close();
            }
        });
        commandsList.put("getScore", new CommandWithoutInput("печатает результат") {
            @Override
            public String process() {
                return getScore();
            }
        });
        commandsList.put("repeatQuestion", new CommandWithoutInput("повторяет текущий вопрос") {
            @Override
            public String process() {
                return repeatQuestion();
            }
        });
        commandsList.put("commandHelp", new CommandWithInput("отображает справку по одной команде") {
            @Override
            public String process(String[] args) {
                return commandHelp(args[0]);
            }
        });
        commandsList.put("addQuestions", new CommandWithInput("добавляет вопросы по указанному пути") {
            @Override
            public String process(String[] args) throws Exception {
                return addQuestions(args[0]);
            }
        });
    }

    private String checkAnswer(String ipnut)
    {
        return quiz.checkAnswer(ipnut);
    }

    public Quiz getQuiz()
    {
        return quiz;
    }

    private String close()
    {
        System.exit(0);
        return null;
    }

    String addQuestions(String path) throws Exception
    {
        try
        {
            quiz.modifyQuestionsList(path);
        }
        catch (java.nio.file.NoSuchFileException wjr)
        {
            return "Такого файла нет";
        }
        return null;
    }

    private String shuffleQuestions()
    {
        quiz.shuffleQuestions();
        return null;
    }

    private String getScore()
    {
        return quiz.getScore();
    }

    private String help()
    {
        StringBuilder stringBuilder = new StringBuilder("");
        commandsList.forEach((x, c) -> stringBuilder.append("Команда ").append(x).append(" - ").append(c.description).append("\n"));
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    private String restart()
    {
        quiz.clearScore();
        return null;
    }

    private String commandHelp(String input)
    {
        if (commandsList.containsKey(input))
        {
            return "Команда" + " " + input + " " + commandsList.get(input).description;
        }
        else
        {
            return "Такой команды нет";
        }
    }

    private String getNewQuestion()
    {
        if (quiz.quizWantsAnswer)
        {
            return "Вы ещё не ответили на предыдущий вопрос";
        }
        else
        {
            return quiz.getNewQuestion();
        }
    }
    private String repeatQuestion()
    {
        return quiz.repeatQuestion();
    }
}

abstract class Command
{
    String description;
    public Command(String description)
    {
        this.description = description;
    }
    public abstract String startProcess(String[] args) throws Exception;
}

abstract class CommandWithoutInput extends Command
{
    protected abstract String process() throws Exception;
    CommandWithoutInput(String description)
    {
        super(description);
    }

    public String startProcess(String[] args) throws Exception
    {
        if (args != null) {
            throw new Exception("Эта команда аргумента не имеет");
        }
        return process();
    }
}

abstract class CommandWithInput extends Command
{
    protected abstract String process(String[] args) throws Exception;
    CommandWithInput(String description)
    {
        super(description);
    }
    public String startProcess(String[] args) throws Exception
    {
        if(args == null)
        {
            throw new Exception("Этой команде нужен аргумент");
        }
        return process(args);
    }

}


