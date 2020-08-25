package kz.qbots.service;



import kz.qbots.dao.DaoFactory;
import kz.qbots.dao.implement.GroupDao;
import kz.qbots.entity.custom.Group;
import kz.qbots.util.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GroupCacheService {

    private static HashMap<Long, Group> groupsMap;
    private static GroupDao groupDao = DaoFactory.getInstance().getGroupDao();

    static {
        groupsMap = new HashMap<>();
        for (Group group : groupDao.getGroups()) {
            groupsMap.put(group.getChatId(), group);
        }
    }

    public static Group checkGroup(Update update, long chatId) {
        Group newGroup = groupsMap.get(chatId);
        Chat chat = UpdateUtil.getChat(update);
        if (newGroup == null) {
            Group group = new Group();
            group.setChatId(chatId);
            group.setNames(chat.getTitle());
            group.setUserName(chat.getUserName());
            save(group);
            groupsMap.put(chatId, group);
        } else if (newGroup.getUserName() == null || !newGroup.getUserName().equals(chat.getUserName()) || !newGroup.getNames().equals(chat.getTitle())) {
            newGroup.setChatId(chatId);
            newGroup.setNames(chat.getTitle());
            newGroup.setUserName(chat.getUserName());
            save(newGroup);
        }
        return groupsMap.get(chatId);
    }

    public static Group get(long chatIdGroup) {
        return groupsMap.getOrDefault(chatIdGroup, null);
    }


    public static void save(Group group) {
        log.info("Change group: {}", group);
        if (groupDao.isExist(group.getChatId())) {
            groupDao.update(group);
        } else {
            groupDao.insert(group);
        }
    }
}
