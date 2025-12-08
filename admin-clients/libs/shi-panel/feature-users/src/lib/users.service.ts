import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetUsersRequest } from './models/get-users-request.model';
import { PostUser } from './models/post-user.model';
import { PutPermissions } from './models/put-permissions.model';
import { PutUser } from './models/put-user.model';
import { UsersState } from './state/user.state';
import { UsersApi } from './users.api';

@Injectable()
export class UsersService {
    private readonly _usersApi = inject(UsersApi);
    private readonly _usersState = inject(UsersState);

    readonly usersListProvider = Object.freeze({
        load: (request: GetUsersRequest) => StateManager.load(
            this._usersState.usersList,
            this._usersApi.getUsers(request).pipe(mapMetadata())
        ),
        getUsersListData$: () => this._usersState.usersList.getValue$().pipe(getListData()),
        getUsersListMetadata$: () => this._usersState.usersList.getValue$().pipe(getMetadata()),
        createUser: (user: PostUser): Observable<{ id: string }> =>
            StateManager.inProgress(
                this._usersState.usersList,
                this._usersApi.postUser(user)
            ),
        deleteUser: (id: string): Observable<void> =>
            StateManager.inProgress(
                this._usersState.usersList,
                this._usersApi.deleteUser(id)
            ),
        loading$: () => this._usersState.usersList.isInProgress$()
    });

    readonly userDetailsProvider = Object.freeze({
        loadUser: (userId?: string) =>
            StateManager.load(
                this._usersState.userDetails,
                this._usersApi.getUser(userId)
            ),
        getUser$: () => this._usersState.userDetails.getValue$(),
        updateUser: (userId: string, user: PutUser): Observable<void> =>
            StateManager.inProgress(
                this._usersState.userDetails,
                this._usersApi.putUser(userId, user)
            ),
        error$: () => this._usersState.userDetails.getError$(),
        clearUser: () => this._usersState.userDetails.setValue(null),
        loading$: () => this._usersState.userDetails.isInProgress$()
    });

    readonly permissions = Object.freeze({
        savePermissions: (userId: number, request: PutPermissions): Observable<void> =>
            StateManager.inProgress(
                this._usersState.userPermissions,
                this._usersApi.putUserPermissions(userId, request)
            ),
        isPermissionsSaving$: () => this._usersState.userPermissions.isInProgress$()
    });
}

