package cn.ltcraft;

import cn.ltcraft.rcon.Rcon;
import cn.ltcraft.rcon.ex.AuthenticationException;
import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class Main extends PluginBase {
    private Config config;
    private Rcon rcon;
    public static Boolean isPE = true;

    public void onLoad(){
        config = this.loadConfig("config.yml");
        config.setIfAbsent("serverAdder��д", "��������ַ,��д���������ַ����");
        config.setIfAbsent("serverAdder", "");
        config.setIfAbsent("serverPort��д", "�������˿�,��д��������˿ڼ���");
        config.setIfAbsent("serverPort", 0);
        config.setIfAbsent("passworld��д", "������Rcon���� ��server.properties�ļ����ҵ�rcon.password");
        config.setIfAbsent("passworld", "");
        config.setIfAbsent("isPe��д", "�����PE��������дtrue �����pc��������дfalse");
        config.setIfAbsent("isPe", true);
        config.setIfAbsent("��ô����Rcon", "��server.properties�ҵ�enable-rcon=off��Ϊon����!�������аٶ�.");
        config.setIfAbsent("ע��", "��д�������һ�¼���/reload");
        config.save();
        super.onLoad();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (rcon!=null){
            try {
                rcon.disconnect();
                rcon = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onEnable(){
        if (!config.getString("serverAdder").equals("") && config.getInt("serverPort")!=0 && !config.getString("passworld").equals("")) {
            isPE = config.getBoolean("isPe");
            if (!connected()){
                getLogger().info("����Rcon������ʧ�ܣ���������ͷ�������ַ��ͨ�ԡ�");
                return;
            }
            JCommandManager.getInstance().register(this, new BlockingCommand(
                    "c", new ArrayList<>(), "����������������÷�/c ����", "/c ����"
            ) {
                @Override
                public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                    if (rcon!=null){
                        String command = utils.listToString(list, ' ');
                        try {
                            commandSender.sendMessageBlocking(command(command));
                        } catch (IOException e) {
                            getLogger().info("��������...");
                            if (!connected()){
                                commandSender.sendMessageBlocking("����Rcon������ʧ�ܣ���������ͷ�������ַ��ͨ�ԡ�");
                                return true;
                            }
                            try {
                                commandSender.sendMessageBlocking(command(command));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                commandSender.sendMessageBlocking("ִ��ʧ�ܣ�");
                            }
                        }
                    }
                    return true;
                }
            });
            super.onEnable();
        }
    }
    public String command(String command) throws IOException {
        String results = rcon.command(command);
        if (results.length()>0) {
            results = utils.clean(results);
            return results;
        }else {
            return "ִ�гɹ������������ؿգ�";
        }
    }
    public boolean connected(){
        try {
            getLogger().info("����"+config.getString("serverAdder")+":"+config.getInt("serverPort")+"...");
            rcon = new Rcon(config.getString("serverAdder"), config.getInt("serverPort"), config.getString("passworld").getBytes());
            return true;
        } catch (IOException| AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
    }
}