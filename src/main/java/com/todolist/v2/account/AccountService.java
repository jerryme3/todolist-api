package com.todolist.v2.account;

import com.todolist.v2.account.dto.AccountRequest;
import com.todolist.v2.account.dto.AccountResponse;
import com.todolist.v2.utils.BasicService;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements BasicService<Account, AccountRequest, AccountResponse> {

	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
}
