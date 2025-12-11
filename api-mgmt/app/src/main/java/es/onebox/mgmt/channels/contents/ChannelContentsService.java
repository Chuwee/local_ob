package es.onebox.mgmt.channels.contents;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.contents.converter.ChannelContentsConverter;
import es.onebox.mgmt.channels.contents.dto.ChannelAuditedTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneErrorDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneErrorsDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralsDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelProfiledTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelProfiledTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockCategory;
import es.onebox.mgmt.channels.contents.enums.ChannelContentType;
import es.onebox.mgmt.channels.contents.enums.ChannelVersion;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAuditedTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelContentClone;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiterals;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlockFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBlockType;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.UserFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Users;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ChannelContentsService {

    private enum OperationScope {
        PORTAL, BOX_OFFICE, MEMBER, PORTAL_OR_MEMBER, OB_CHANNEL, NOT_EXTERNAL
    }

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;
    private final UsersRepository usersRepository;

    @Autowired
    public ChannelContentsService(ChannelContentsRepository channelContentsRepository, ChannelsHelper channelsHelper,
                                  MasterdataService masterdataService, UsersRepository usersRepository) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
        this.usersRepository = usersRepository;
    }

    public ChannelLiteralsDTO getChannelLiterals(final Long channelId, final String languageCode, final String key, final ChannelVersion channelVersion) {
        validateRequest(channelId, OperationScope.PORTAL_OR_MEMBER, languageCode);
        ChannelLiterals result = channelContentsRepository.getChannelLiterals(channelId, convertLanguage(languageCode), key, channelVersion);
        return ChannelContentsConverter.toDTO(result);
    }

    public void upsertChannelLiterals(final Long channelId, final String languageCode, final ChannelLiteralsDTO body, final ChannelVersion channelVersion) {
        validateRequest(channelId, OperationScope.PORTAL_OR_MEMBER, languageCode);
        ChannelLiterals out = ChannelContentsConverter.toDTO(body);
        channelContentsRepository.createOrUpdateChannelLiterals(channelId, convertLanguage(languageCode), out, channelVersion);
    }

    public ChannelTextBlocksDTO getChannelTextBlocks(final Long channelId, final String languageCode, ChannelBlockCategory category,
                                                     List<ChannelBlockType> type) {
        validateChannelTextBlockCategory(category);
        validateRequest(channelId, OperationScope.NOT_EXTERNAL, languageCode);
        List<ChannelTextBlock> result = channelContentsRepository.getChannelTextBlocks(channelId, new ChannelTextBlockFilter(type, category, convertLanguage(languageCode)));
        return ChannelContentsConverter.toDTO(result);
    }

    public List<ChannelAuditedTextBlockDTO> getChannelTextBlockHistory(final Long channelId, final Long blockId,
                                                                       final String languageCode) {
        validateRequest(channelId, OperationScope.NOT_EXTERNAL, languageCode);
        List<ChannelAuditedTextBlock> result = channelContentsRepository.getChannelTextBlockHistoricalData(channelId, blockId,
                convertLanguage(languageCode));
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        List<Long> userIds = result.stream().map(ChannelAuditedTextBlock::getUserId).distinct().collect(Collectors.toList());
        UserFilter request = UserFilter.builder().id(userIds).operatorId(SecurityUtils.getUserOperatorId()).build();
        Users response = this.usersRepository.getUsers(request);
        Map<Long, String> users = response.getData().stream().collect(Collectors.toMap(User::getId, User::getUsername));
        return ChannelContentsConverter.toHistoricalDTO(result, users);
    }

    public void updateChannelTextBlocks(final Long channelId, ChannelBlockCategory category, UpdateChannelTextBlocksDTO body) {
        validateChannelTextBlockCategory(category);
        String[] langs = body.stream().map(UpdateChannelTextBlockDTO::getLanguage).distinct().toArray(String[]::new);
        validateRequest(channelId, OperationScope.NOT_EXTERNAL, langs);
        UpdateChannelTextBlocks out = ChannelContentsConverter.toDTO(body);
        this.channelContentsRepository.updateChannelTextBlocks(channelId, out);
    }

    public void updateChannelProfiledTextBlocks(final Long channelId, final Long contentId, UpdateChannelProfiledTextBlocksDTO body) {
        String[] langs = body.stream().map(UpdateChannelProfiledTextBlockDTO::getLanguage).distinct().toArray(String[]::new);
        validateRequest(channelId, OperationScope.BOX_OFFICE, langs);
        List<UpdateChannelProfiledTextBlock> out = ChannelContentsConverter.toDTO(body);
        this.channelContentsRepository.updateProfiledChannelTextBlocks(channelId, contentId, out);
    }

    public ChannelContentsCloneErrorsDTO cloneChannelContents(final Long channelId, final ChannelContentsCloneDTO channelContentsCloneDTO) {
        ChannelResponse channelResponse = validateRequest(channelId, OperationScope.OB_CHANNEL);
        ChannelResponse sourceChannelResponse = validateRequest(channelContentsCloneDTO.getChannelId(), OperationScope.OB_CHANNEL);
        ChannelContentClone channelContentClone = ChannelContentsConverter.toMs(channelContentsCloneDTO);

        if (!channelResponse.getType().equals(sourceChannelResponse.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNELS_TYPE_MISMATCH);
        }

        // warning: this runnable breaks the propagation of service preview headers
        Map<ChannelContentType, Runnable> map = new HashMap<ChannelContentType, Runnable>() {{
            put(ChannelContentType.TICKETS, () -> channelContentsRepository.cloneTicketContents(channelId, channelContentClone));
            put(ChannelContentType.TEXTS, () -> {
                if (ChannelType.OB_PORTAL.equals(channelResponse.getType())) {
                    channelContentsRepository.cloneTextContents(channelId, channelContentClone);
                }
            });
            put(ChannelContentType.GRAPHIC_CONTENTS, () -> channelContentsRepository.clonePurchaseContents(channelId, channelContentClone));
            put(ChannelContentType.COMMUNICATION_ELEMENTS, () -> channelContentsRepository.cloneTextBlocksContents(channelId, channelContentClone));
        }};


        final Map<ChannelContentType, CompletableFuture<Void>> completableFutures = channelContentsCloneDTO.getContents().stream()
                .collect(Collectors.toMap(
                        type -> type,
                        type -> CompletableFuture.runAsync(map.get(type))
                ));

        return completableFutures.keySet().stream()
                .map(type -> {
                    try {
                        completableFutures.get(type).join();
                        return null;
                    } catch (Exception e) {
                        return new ChannelContentsCloneErrorDTO(type, e.getCause().getMessage());
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ChannelContentsCloneErrorsDTO::new));
    }

    private static void validateChannelTextBlockCategory(ChannelBlockCategory category) {
        if (category == null) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.NOT_FOUND);
        }
    }

    private void validateRequest(final Long channelId, @Nonnull final OperationScope scope, String... languageCodes) {
        ChannelResponse channelResponse = validateRequest(channelId, scope);
        channelsHelper.validateLanguage(channelResponse, languageCodes);
    }

    private ChannelResponse validateRequest(final Long channelId, @Nonnull final OperationScope scope) {
        ChannelResponse channelResponse = this.channelsHelper.getAndCheckChannel(channelId);
        validateType(channelResponse.getType(), scope);
        return channelResponse;
    }

    private static void validateType(final ChannelType type, final OperationScope scope) {
        switch (scope) {
            case PORTAL -> ChannelUtils.validateOBPortal(type);
            case BOX_OFFICE -> ChannelUtils.validateOBBoxOffice(type);
            case OB_CHANNEL -> ChannelUtils.validateOBChannel(type);
            case MEMBER -> ChannelUtils.validateMember(type);
            case PORTAL_OR_MEMBER -> ChannelUtils.validateOBPortalOrMembers(type);
            case NOT_EXTERNAL -> ChannelUtils.validateNotExternal(type);
            default -> throw ExceptionBuilder.build(ApiMgmtErrorCode.NOT_IMPLEMENTED);
        }
    }

    private static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }
}
